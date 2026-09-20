package com.swyp.haruup.presentation.home

import androidx.lifecycle.ViewModel
import com.swyp.haruup.presentation.mission.MissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalTime
import javax.inject.Inject

data class HomeUiState(
    val memberInfo: HomeMemberInfo = HomeMemberInfo(),
    /** 연속 미션 달성일 */
    val challengeDay: Int = 0,
    val bubbleIndex: Int = 0,
    val isLoading: Boolean = false,
    /** 오늘 고른 미션 */
    val todayMissions: List<MissionItem> = emptyList(),
    /** 완료 처리한 미션 ID */
    val completedMissionIds: Set<Int> = emptySet(),
    val isTooltipVisible: Boolean = false,
) {
    val hasMissions: Boolean = todayMissions.isNotEmpty()

    /** 5개를 다 골랐으면 추가 버튼을 감춥니다. */
    val canAddMission: Boolean = todayMissions.size < MAX_MISSION_COUNT

    fun isCompleted(missionId: Int): Boolean = missionId in completedMissionIds

    /**
     * 캐릭터 말풍선 문구. 닉네임과 관심사가 들어가므로 상태에서 만듭니다.
     * iOS HomeHeaderView.messages 와 같은 순서입니다.
     */
    val bubbleMessages: List<String> = listOf(
        "오늘 하루도 함께 나아가볼까요?",
        "${memberInfo.nickname.ifBlank { "사용자" }}님의 " +
            "${memberInfo.interest.ifBlank { "외국어 공부" }}을(를) 응원해요!",
        "큰 변화는 필요 없어요. 작은 미션 하나면 충분해요.",
    )

    val bubbleText: String = bubbleMessages[bubbleIndex % bubbleMessages.size]

    companion object {
        /** 하루 최대 미션 개수 */
        const val MAX_MISSION_COUNT = 5
    }
}

/**
 * 홈 화면. iOS 의 HomeViewModel 에 대응합니다.
 *
 * 이번 단계에서는 상단 영역만 다룹니다.
 * 오늘의 미션 목록과 바텀시트는 이어서 붙입니다.
 */
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /** 캐릭터나 말풍선을 누르면 다음 문구로 넘깁니다. */
    fun onBubbleClick() {
        _uiState.update { it.copy(bubbleIndex = it.bubbleIndex + 1) }
    }

    fun onInfoClick() {
        _uiState.update { it.copy(isTooltipVisible = !it.isTooltipVisible) }
    }

    /** TODO: 프로필 조회 API 로 교체 */
    fun setMemberInfo(memberInfo: HomeMemberInfo) {
        _uiState.update { it.copy(memberInfo = memberInfo) }
    }

    /** TODO: 오늘의 미션 조회 API 로 교체 */
    fun setTodayMissions(missions: List<MissionItem>) {
        _uiState.update { it.copy(todayMissions = missions) }
    }

    companion object {
        /** 6시부터 19시 전까지를 낮으로 봅니다. (iOS isDaytime 과 동일) */
        fun isDaytime(now: LocalTime = LocalTime.now()): Boolean =
            now.hour in 6..18
    }
}
