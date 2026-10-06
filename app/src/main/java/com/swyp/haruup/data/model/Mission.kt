package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/** 미션 상태. 서버가 받는 값입니다. */
object MissionStatus {
    /** 오늘 고른, 아직 안 끝낸 미션 */
    const val ACTIVE = "ACTIVE"

    /** 지운 미션 */
    const val INACTIVE = "INACTIVE"

    const val COMPLETED = "COMPLETED"
}

/**
 * GET /api/member/mission 의 미션 한 건. iOS 의 MissionListDTO 에 대응합니다.
 *
 * 챗봇이 만든 미션은 fullPath / directFullPath 가 비어 있습니다.
 */
@Serializable
data class MissionListItem(
    val id: Int,
    val memberId: Int = 0,
    val memberInterestId: Int = 0,
    val missionStatus: String,
    val expEarned: Int = 0,
    val targetDate: String = "",
    val missionContent: String,
    val missionDescription: String? = null,
    val difficulty: Int = 0,
    val fullPath: List<String>? = null,
    val directFullPath: List<String>? = null,
)

/** POST /api/member/mission/select 요청 body */
@Serializable
data class SelectMissionRequest(
    val memberMissionIds: List<Int>,
)

/** PUT /api/member/mission/status 요청 body. 한 번에 여러 건을 보낼 수 있습니다. */
@Serializable
data class MissionStatusRequest(
    val missions: List<MemberMissionStatus>,
)

@Serializable
data class MemberMissionStatus(
    val memberMissionId: Int,
    val missionStatus: String,
)

/** 하루치 달성 여부. iOS 의 ChallengeDataDTO 에 대응합니다. */
@Serializable
data class ChallengeDate(
    val targetDate: String,
    val isCompleted: Boolean = false,
)

/** 한 달의 일별 미션 현황. iOS 의 HistoryDTO 에 대응합니다. */
@Serializable
data class MonthlyMissionData(
    val missionCounts: List<MissionCount> = emptyList(),
    val totalMissionCount: Int = 0,
    val totalCompletedDays: Int = 0,
)

@Serializable
data class MissionCount(
    val targetDate: String,
    val completedCount: Int = 0,
)

/** 성장 차트용 월별 달성일수. iOS 의 GrowthDataDTO 에 대응합니다. */
@Serializable
data class GrowthData(
    val monthlyData: List<MonthlyAttendance> = emptyList(),
)

@Serializable
data class MonthlyAttendance(
    /** yyyy-MM */
    val targetMonth: String,
    val completedDays: Int = 0,
)
