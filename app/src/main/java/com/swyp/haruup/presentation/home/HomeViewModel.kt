package com.swyp.haruup.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.core.util.MissionDates
import com.swyp.haruup.data.model.MemberMissionStatus
import com.swyp.haruup.data.model.MissionStatus
import com.swyp.haruup.data.model.MissionStatusRequest
import com.swyp.haruup.network.service.MemberService
import com.swyp.haruup.network.service.MissionService
import com.swyp.haruup.presentation.mission.MissionItem
import com.swyp.haruup.presentation.mission.toMissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
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
    /** 상세 시트를 열어 둔 미션. null 이면 시트가 닫힌 상태입니다. */
    val actionSheetMission: MissionItem? = null,
    /** 삭제 확인 시트를 열어 둔 미션 */
    val deleteConfirmMission: MissionItem? = null,
    /** 완료 축하 알림에 표시할 경험치. null 이면 닫힌 상태입니다. */
    val completedExp: Int? = null,
    val isStreakSheetVisible: Boolean = false,
    val dailyMissions: List<DailyMission> = emptyList(),
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
class HomeViewModel @Inject constructor(
    private val missionService: MissionService,
    private val memberService: MemberService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodayMissions()
        loadChallenge()
    }

    /**
     * 홈 상단의 캐릭터와 경험치입니다.
     *
     * 화면이 보일 때마다 호출되므로 여기서는 init 에서 부르지 않습니다. 두 번 받아오게 됩니다.
     *
     * 못 받아오면 지금 보여 주고 있는 값을 그대로 둡니다.
     * 통신이 한 번 실패했다고 레벨과 닉네임이 사라지면 더 이상해 보이기 때문입니다.
     */
    fun loadMemberInfo() {
        viewModelScope.launch {
            val data = runCatching { memberService.homeMemberInfo() }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data
                ?.firstOrNull()
                ?: return@launch

            _uiState.update { it.copy(memberInfo = data.toHomeMemberInfo()) }
        }
    }

    /**
     * 오늘 고른 미션을 받아옵니다.
     *
     * 완료한 미션도 함께 받아 목록에 남겨 둡니다. 지운 미션만 빠집니다. (iOS 와 같은 조회 조건)
     */
    private fun loadTodayMissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val missions = runCatching {
                missionService.missions(
                    missionStatus = "${MissionStatus.COMPLETED},${MissionStatus.ACTIVE}",
                    targetDate = MissionDates.format(LocalDate.now()),
                )
            }.getOrNull()
                ?.takeIf { it.success }
                ?.data

            if (missions == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            _uiState.update { state ->
                state.copy(
                    todayMissions = missions.map { it.toMissionItem() },
                    completedMissionIds = missions
                        .filter { it.missionStatus == MissionStatus.COMPLETED }
                        .map { it.id }
                        .toSet(),
                    isLoading = false,
                )
            }
        }
    }

    /** 연속 달성일과 시트에 그릴 7칸을 함께 만듭니다. */
    private fun loadChallenge() {
        viewModelScope.launch {
            val (startDate, endDate) = MissionDates.challengeRange()

            val days = runCatching { missionService.completionStatus(startDate, endDate) }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data
                ?: return@launch

            _uiState.update {
                it.copy(challengeDay = challengeStreak(days), dailyMissions = challengeDays(days))
            }
        }
    }

    /** 캐릭터나 말풍선을 누르면 다음 문구로 넘깁니다. */
    fun onBubbleClick() {
        _uiState.update { it.copy(bubbleIndex = it.bubbleIndex + 1) }
    }

    fun onInfoClick() {
        _uiState.update { it.copy(isTooltipVisible = !it.isTooltipVisible) }
    }

    // MARK: - 미션 상세 시트

    fun onMissionSettingClick(mission: MissionItem) {
        _uiState.update { it.copy(actionSheetMission = mission) }
    }

    fun onActionSheetDismiss() {
        _uiState.update { it.copy(actionSheetMission = null) }
    }

    /**
     * 미션을 완료 처리합니다.
     *
     * 서버에 먼저 알리고 성공했을 때만 화면을 바꿉니다.
     * 실패했는데 완료로 보여 주면 다음에 들어왔을 때 되돌아가 있어 더 혼란스럽기 때문입니다.
     */
    fun onCompleteClick() {
        val mission = _uiState.value.actionSheetMission ?: return

        _uiState.update { it.copy(actionSheetMission = null) }

        viewModelScope.launch {
            if (!updateStatus(mission.id, MissionStatus.COMPLETED)) return@launch

            _uiState.update {
                it.copy(
                    completedMissionIds = it.completedMissionIds + mission.id,
                    completedExp = mission.expEarned,
                )
            }

            // 달성일이 늘었을 수 있어 다시 받아옵니다.
            loadChallenge()
        }
    }

    fun onCompleteConfirm() {
        _uiState.update { it.copy(completedExp = null) }
    }

    // MARK: - 미션 삭제

    /** 삭제는 한 번 더 확인을 받습니다. */
    fun onDeleteClick() {
        _uiState.update { it.copy(deleteConfirmMission = it.actionSheetMission, actionSheetMission = null) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(deleteConfirmMission = null) }
    }

    /** 미션을 목록에서 지웁니다. 서버에서는 INACTIVE 로 바뀝니다. */
    fun onDeleteConfirm() {
        val mission = _uiState.value.deleteConfirmMission ?: return

        _uiState.update { it.copy(deleteConfirmMission = null) }

        viewModelScope.launch {
            if (!updateStatus(mission.id, MissionStatus.INACTIVE)) return@launch

            _uiState.update {
                it.copy(
                    todayMissions = it.todayMissions.filterNot { item -> item.id == mission.id },
                    completedMissionIds = it.completedMissionIds - mission.id,
                )
            }

            loadChallenge()
        }
    }

    /** 완료와 삭제는 상태 값만 다릅니다. */
    private suspend fun updateStatus(missionId: Int, status: String): Boolean =
        runCatching {
            missionService.updateStatus(
                MissionStatusRequest(listOf(MemberMissionStatus(missionId, status)))
            )
        }.getOrNull()?.success == true

    // MARK: - 연속 달성 시트

    fun onChallengeClick() {
        _uiState.update { it.copy(isStreakSheetVisible = true) }
    }

    fun onStreakSheetDismiss() {
        _uiState.update { it.copy(isStreakSheetVisible = false) }
    }

    companion object {
        /** 6시부터 19시 전까지를 낮으로 봅니다. (iOS isDaytime 과 동일) */
        fun isDaytime(now: LocalTime = LocalTime.now()): Boolean =
            now.hour in 6..18
    }
}
