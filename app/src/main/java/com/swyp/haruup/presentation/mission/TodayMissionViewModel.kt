package com.swyp.haruup.presentation.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.data.model.RetryMissionRequest
import com.swyp.haruup.data.model.SelectMissionRequest
import com.swyp.haruup.network.service.MissionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TodayMissionUiState(
    val missions: List<MissionItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val filter: MissionDifficultyFilter = MissionDifficultyFilter.ALL,
    /** 지금까지 쓴 재추천 횟수 */
    val retryCount: Int = 0,
    val isLoading: Boolean = false,
) {
    val filteredMissions: List<MissionItem> =
        missions.filter { filter.matches(it.difficulty) }

    val selectedCount: Int = selectedIds.size

    /** 아직 고를 수 있는 개수. 하단 영역에 표시합니다. */
    val remainingCount: Int = MAX_SELECTION - selectedCount

    val isSelectionFull: Boolean = selectedCount >= MAX_SELECTION

    val isCompleteEnabled: Boolean = selectedCount > 0 && !isLoading

    /** 재추천은 정해진 횟수까지만 쓸 수 있습니다. */
    val isRetryEnabled: Boolean = retryCount < MAX_RETRY && !isLoading

    /** 예: "다른 추천 2/5회" */
    val retryLabel: String = "다른 추천 $retryCount/${MAX_RETRY}회"

    fun isSelected(missionId: Int): Boolean = missionId in selectedIds

    /** 5개를 다 골랐고 이 미션은 선택되지 않았다면 더 고를 수 없습니다. */
    fun isDisabled(missionId: Int): Boolean = isSelectionFull && !isSelected(missionId)

    companion object {
        /** 하루 최대 선택 개수 */
        const val MAX_SELECTION = 5

        /** 하루 최대 재추천 횟수 */
        const val MAX_RETRY = 5
    }
}

/**
 * 오늘의 미션 선택. iOS 의 TodayMissionListViewModel 중 챗봇 플로우 경로에 대응합니다.
 *
 * 챗봇 완료 직후 진입은 answer 응답의 미션을 그대로 표시하고 추천 API 를 다시 부르지 않습니다.
 */
@HiltViewModel
class TodayMissionViewModel @Inject constructor(
    private val missionService: MissionService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayMissionUiState())
    val uiState: StateFlow<TodayMissionUiState> = _uiState.asStateFlow()

    /** 챗봇이 만들어 준 미션을 그대로 씁니다. 한 번만 반영합니다. */
    fun setMissions(missions: List<MissionItem>) {
        if (_uiState.value.missions.isNotEmpty()) return
        _uiState.update { it.copy(missions = missions) }
    }

    fun onFilterClick(filter: MissionDifficultyFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    /**
     * 다른 미션으로 다시 추천받습니다.
     *
     * 이미 고른 미션은 그대로 두고 나머지 자리만 새 미션으로 채웁니다.
     * 고르던 것이 사라지면 처음부터 다시 봐야 하기 때문입니다. (iOS 와 같은 규칙)
     *
     * 실패하면 목록을 건드리지 않습니다. 횟수만 쓰고 빈 화면이 되는 쪽이 더 나쁩니다.
     */
    fun onRetryClick() {
        val state = _uiState.value
        if (!state.isRetryEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val data = runCatching {
                // 챗봇으로 만든 미션이라 제외 목록을 보내지 않습니다. (iOS MissionSource.chatbot 과 동일)
                missionService.retryMissions(RetryMissionRequest(memberInterestId = CHATBOT_INTEREST_ID))
            }.getOrNull()
                ?.takeIf { it.success }
                ?.data

            if (data == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val fresh = data.missions
                .firstOrNull { it.memberInterestId == CHATBOT_INTEREST_ID }
                ?.data
                ?.map { item ->
                    MissionItem(
                        id = item.memberMissionId,
                        content = item.content,
                        description = item.missionDescription,
                        difficulty = MissionDifficulty.from(item.difficulty),
                        expEarned = item.expEarned,
                    )
                }
                .orEmpty()

            _uiState.update { current ->
                val kept = current.missions.filter { it.id in current.selectedIds }

                // 목록 크기는 서버가 준 추천 세트를 따릅니다. 고른 것을 뺀 만큼만 새로 채웁니다.
                val refill = fresh.take((fresh.size - kept.size).coerceAtLeast(0))

                current.copy(
                    missions = kept + refill,
                    retryCount = data.retryCount,
                    isLoading = false,
                )
            }
        }
    }

    /**
     * 미션을 고르거나 해제합니다.
     * 이미 5개를 골랐으면 새로 고를 수 없고, 해제는 언제나 됩니다. (iOS 와 동일)
     */
    fun onMissionClick(missionId: Int) {
        _uiState.update { state ->
            val next = state.selectedIds.toMutableSet()

            if (!next.remove(missionId)) {
                if (next.size >= TodayMissionUiState.MAX_SELECTION) return@update state
                next.add(missionId)
            }

            state.copy(selectedIds = next)
        }
    }

    /**
     * 미션 선택을 마칩니다.
     *
     * 고른 미션을 서버에 확정하고, 이 화면이 큐레이션의 마지막이라 온보딩 완료도 함께 기록합니다.
     * 그래야 다음에 앱을 열었을 때 스플래시가 바로 메인 탭으로 보냅니다.
     * (iOS 는 AppCoordinator 의 큐레이션 onFinish 에서 같은 일을 합니다)
     *
     * 확정에 실패해도 화면은 넘깁니다. 여기서 막으면 큐레이션을 처음부터 다시 해야 하는데,
     * 홈에서 미션을 다시 고를 수 있어 되돌리는 비용이 훨씬 작기 때문입니다.
     */
    fun onCompleteClick(onDone: (selectedIds: List<Int>) -> Unit) {
        val selectedIds = _uiState.value.selectedIds.toList()
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching { missionService.selectMissions(SelectMissionRequest(selectedIds)) }

            tokenStorage.setOnboardingCompleted(true)
            _uiState.update { it.copy(isLoading = false) }
            onDone(selectedIds)
        }
    }

    companion object {
        /**
         * 챗봇 목표로 만든 미션을 가리키는 값입니다.
         * 관심사에서 만든 미션과 구분하려고 서버가 0 을 씁니다. (iOS MissionSource 와 동일)
         */
        const val CHATBOT_INTEREST_ID = 0
    }
}
