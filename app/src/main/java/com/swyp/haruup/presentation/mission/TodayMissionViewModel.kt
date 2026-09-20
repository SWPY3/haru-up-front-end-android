package com.swyp.haruup.presentation.mission

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TodayMissionUiState(
    val missions: List<MissionItem> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val filter: MissionDifficultyFilter = MissionDifficultyFilter.ALL,
    val isLoading: Boolean = false,
) {
    val filteredMissions: List<MissionItem> =
        missions.filter { filter.matches(it.difficulty) }

    val selectedCount: Int = selectedIds.size

    /** 아직 고를 수 있는 개수. 하단 영역에 표시합니다. */
    val remainingCount: Int = MAX_SELECTION - selectedCount

    val isSelectionFull: Boolean = selectedCount >= MAX_SELECTION

    val isCompleteEnabled: Boolean = selectedCount > 0 && !isLoading

    fun isSelected(missionId: Int): Boolean = missionId in selectedIds

    /** 5개를 다 골랐고 이 미션은 선택되지 않았다면 더 고를 수 없습니다. */
    fun isDisabled(missionId: Int): Boolean = isSelectionFull && !isSelected(missionId)

    companion object {
        /** 하루 최대 선택 개수 */
        const val MAX_SELECTION = 5
    }
}

/**
 * 오늘의 미션 선택. iOS 의 TodayMissionListViewModel 중 챗봇 플로우 경로에 대응합니다.
 *
 * 챗봇 완료 직후 진입은 answer 응답의 미션을 그대로 표시하고 추천 API 를 다시 부르지 않습니다.
 */
@HiltViewModel
class TodayMissionViewModel @Inject constructor() : ViewModel() {

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
     * 고른 미션 ID 를 돌려줍니다.
     * TODO: MissionService 의 선택 API(api/member/mission/select) 를 호출하도록 교체
     */
    fun selectedMissionIds(): List<Int> = _uiState.value.selectedIds.toList()
}
