package com.swyp.haruup.presentation.mission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.core.util.MissionDates
import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.data.model.RecommendMissionRequest
import com.swyp.haruup.data.model.RetryMissionData
import com.swyp.haruup.data.model.RetryMissionItem
import com.swyp.haruup.data.model.RetryMissionRequest
import com.swyp.haruup.data.model.SelectMissionRequest
import com.swyp.haruup.network.service.InterestService
import com.swyp.haruup.network.service.MissionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

data class TodayMissionUiState(
    val missions: List<MissionItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val filter: MissionDifficultyFilter = MissionDifficultyFilter.ALL,
    /** 지금까지 쓴 재추천 횟수 */
    val retryCount: Int = 0,
    val isLoading: Boolean = false,
    /**
     * 오늘 이미 고른 미션 수. 홈에서 미션을 더할 때만 0 보다 큽니다.
     * 목록에는 나오지 않지만 하루 5개 한도에는 들어갑니다.
     */
    val fixedCount: Int = 0,
    /** 미션을 고르기 전 소개 화면. 홈에서 처음 들어온 날 한 번만 보여 줍니다. */
    val isIntroVisible: Boolean = false,
    /** 홈에서 들어왔을 때만 닫을 수 있습니다. 큐레이션 끝에서는 미션을 골라야 넘어갑니다. */
    val isClosable: Boolean = false,
    /** 추천을 받아오지 못했을 때. 다시 불러오기 버튼을 보여 줍니다. */
    val isLoadFailed: Boolean = false,
    val errorMessage: String? = null,
) {
    val filteredMissions: List<MissionItem> =
        missions.filter { filter.matches(it.difficulty) }

    val selectedCount: Int = fixedCount + selectedIds.size

    /** 아직 고를 수 있는 개수. 하단 영역에 표시합니다. */
    val remainingCount: Int = (MAX_SELECTION - selectedCount).coerceAtLeast(0)

    val isSelectionFull: Boolean = selectedCount >= MAX_SELECTION

    val isCompleteEnabled: Boolean = selectedIds.isNotEmpty() && !isLoading

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
 * 오늘의 미션 선택. iOS 의 TodayMissionCoordinator 와 TodayMissionListViewModel 에 대응합니다.
 *
 * 두 곳에서 들어옵니다.
 * - 큐레이션 끝: 챗봇이 만든 미션을 그대로 보여 주고 추천 API 를 다시 부르지 않습니다. ([setMissions])
 * - 홈: 회원의 관심사로 추천을 받아옵니다. 이미 고른 미션은 빼고 보여 줍니다. ([startFromHome])
 */
@HiltViewModel
class TodayMissionViewModel @Inject constructor(
    private val missionService: MissionService,
    private val interestService: InterestService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodayMissionUiState())
    val uiState: StateFlow<TodayMissionUiState> = _uiState.asStateFlow()

    /** 큐레이션 끝에서 들어오면 챗봇 미션입니다. 홈에서는 추천을 받을 때 정합니다. */
    private var source: MissionSource = MissionSource.Chatbot

    private var isFromHome = false

    /** 오늘 이미 고른 미션. 추천 목록에서 빼고, 확정할 때도 다시 보내지 않습니다. */
    private var fixedIds: Set<Int> = emptySet()

    /** 챗봇이 만들어 준 미션을 그대로 씁니다. 한 번만 반영합니다. */
    fun setMissions(missions: List<MissionItem>) {
        if (_uiState.value.missions.isNotEmpty()) return
        _uiState.update { it.copy(missions = missions) }
    }

    /**
     * 홈에서 들어왔을 때 한 번 부릅니다.
     *
     * 소개 화면은 처음 고르러 온 날 한 번만 보여 줍니다.
     * 이미 고른 미션이 있다면 더하러 온 것이라 바로 목록을 보여 줍니다. (iOS 와 같은 규칙)
     * 소개 화면을 보는 동안 추천을 미리 받아 둡니다.
     */
    fun startFromHome(fixedIds: List<Int>, today: LocalDate = LocalDate.now()) {
        if (isFromHome) return
        isFromHome = true
        this.fixedIds = fixedIds.toSet()

        viewModelScope.launch {
            val todayText = MissionDates.format(today)
            val showIntro = fixedIds.isEmpty() && tokenStorage.getLastMissionIntroDate() != todayText
            if (showIntro) tokenStorage.setLastMissionIntroDate(todayText)

            _uiState.update {
                it.copy(
                    fixedCount = this@TodayMissionViewModel.fixedIds.size,
                    isIntroVisible = showIntro,
                    isClosable = true,
                )
            }

            loadRecommendations()
        }
    }

    fun onIntroConfirm() {
        _uiState.update { it.copy(isIntroVisible = false) }
    }

    fun onReloadClick() {
        if (_uiState.value.isLoading) return
        viewModelScope.launch { loadRecommendations() }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * 회원의 관심사로 오늘의 미션을 추천받습니다.
     *
     * 관심사를 어디서 받을지 먼저 정합니다. 관심사가 없으면 챗봇 목표로 만든 미션을 받습니다.
     */
    private suspend fun loadRecommendations() {
        _uiState.update { it.copy(isLoading = true, isLoadFailed = false) }

        val resolved = resolveSource()
        val data = resolved?.let { source ->
            runCatching { missionService.recommendMissions(RecommendMissionRequest(source.recommendIds)) }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data
        }

        if (resolved == null || data == null) {
            _uiState.update {
                it.copy(isLoading = false, isLoadFailed = true, errorMessage = LOAD_ERROR_MESSAGE)
            }
            return
        }

        source = resolved
        val missions = data.missionsFor(resolved.recommendIds.toSet())
            .filterNot { it.id in fixedIds }

        _uiState.update {
            it.copy(
                missions = missions,
                selectedIds = emptySet(),
                retryCount = data.retryCount,
                isLoading = false,
            )
        }
    }

    /** 관심사를 하나도 고르지 않았으면 챗봇 미션입니다. 받아오지 못하면 null 입니다. */
    private suspend fun resolveSource(): MissionSource? =
        runCatching { interestService.memberInterests() }
            .getOrNull()
            ?.let { response -> MissionSource.from(response.interests.map { it.memberInterestId }) }

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

            val request = RetryMissionRequest(
                memberInterestId = source.retryId,
                // 관심사 미션일 때만 고른 미션을 빼 달라고 합니다. (iOS MissionSource 와 같은 규칙)
                excludeMemberMissionIds = state.selectedIds.toList().takeIf { source.sendsExcludeIds },
            )

            val data = runCatching { missionService.retryMissions(request) }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data

            if (data == null) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = RETRY_ERROR_MESSAGE.takeIf { isFromHome })
                }
                return@launch
            }

            val fresh = data.missionsFor(setOf(source.retryId))
                .filterNot { it.id in fixedIds }

            _uiState.update { current ->
                val kept = current.missions.filter { it.id in current.selectedIds }
                val freshOnly = fresh.filterNot { mission -> kept.any { it.id == mission.id } }

                // 목록 크기는 서버가 준 추천 세트를 따릅니다. 고른 것을 뺀 만큼만 새로 채웁니다.
                val refill = freshOnly.take((fresh.size - kept.size).coerceAtLeast(0))

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
     * 오늘 고른 것까지 합쳐 5개가 되면 새로 고를 수 없고, 해제는 언제나 됩니다. (iOS 와 동일)
     */
    fun onMissionClick(missionId: Int) {
        _uiState.update { state ->
            val next = state.selectedIds.toMutableSet()

            if (!next.remove(missionId)) {
                if (state.isSelectionFull) return@update state
                next.add(missionId)
            }

            state.copy(selectedIds = next)
        }
    }

    /**
     * 미션 선택을 마칩니다.
     *
     * 큐레이션 끝에서는 이 화면이 마지막이라 온보딩 완료도 함께 기록합니다.
     * 그래야 다음에 앱을 열었을 때 스플래시가 바로 메인 탭으로 보냅니다.
     * (iOS 는 AppCoordinator 의 큐레이션 onFinish 에서 같은 일을 합니다)
     * 이때는 확정에 실패해도 화면을 넘깁니다. 여기서 막으면 큐레이션을 처음부터 다시 해야 하는데,
     * 홈에서 미션을 다시 고를 수 있어 되돌리는 비용이 훨씬 작기 때문입니다.
     *
     * 홈에서는 확정에 성공했을 때만 닫습니다. 닫혔는데 홈에 미션이 없으면 고른 게 사라진 것처럼 보입니다.
     */
    fun onCompleteClick(onDone: (selectedIds: List<Int>) -> Unit) {
        val state = _uiState.value
        if (state.isLoading) return
        val selectedIds = state.selectedIds.toList()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isSaved = runCatching { missionService.selectMissions(SelectMissionRequest(selectedIds)) }
                .getOrNull()
                ?.success == true

            if (isFromHome) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = SELECT_ERROR_MESSAGE.takeUnless { isSaved })
                }
                if (isSaved) onDone(selectedIds)
                return@launch
            }

            tokenStorage.setOnboardingCompleted(true)
            _uiState.update { it.copy(isLoading = false) }
            onDone(selectedIds)
        }
    }

    /** 응답에서 원하는 관심사의 미션만 꺼냅니다. */
    private fun RetryMissionData.missionsFor(memberInterestIds: Set<Int>): List<MissionItem> =
        missions
            .filter { it.memberInterestId in memberInterestIds }
            .flatMap { group -> group.data.map { it.toMissionItem() } }

    private fun RetryMissionItem.toMissionItem() = MissionItem(
        id = memberMissionId,
        content = content,
        description = missionDescription,
        difficulty = MissionDifficulty.from(difficulty),
        expEarned = expEarned,
    )

    companion object {
        /**
         * 챗봇 목표로 만든 미션을 가리키는 값입니다.
         * 관심사에서 만든 미션과 구분하려고 서버가 0 을 씁니다. (iOS MissionSource 와 동일)
         */
        const val CHATBOT_INTEREST_ID = MissionSource.CHATBOT_INTEREST_ID

        const val LOAD_ERROR_MESSAGE = "미션을 불러오지 못했어요. 잠시 후 다시 시도해주세요."
        const val RETRY_ERROR_MESSAGE = "다른 미션을 불러오지 못했어요."
        const val SELECT_ERROR_MESSAGE = "미션을 저장하지 못했어요. 다시 시도해주세요."
    }
}
