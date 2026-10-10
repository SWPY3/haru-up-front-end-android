package com.swyp.haruup.data.model

import kotlinx.serialization.SerialName
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

/**
 * POST /api/member/mission/retry 요청 body. iOS 의 RetryRecommendRequestDTO 에 대응합니다.
 *
 * 챗봇으로 만든 미션은 excludeMemberMissionIds 를 보내지 않습니다.
 * 관심사 기반 추천일 때만 이미 고른 미션을 빼 달라고 알립니다. (iOS MissionSource 와 같은 규칙)
 */
@Serializable
data class RetryMissionRequest(
    val memberInterestId: Int,
    val excludeMemberMissionIds: List<Int>? = null,
)

/** 재추천 응답. 관심사별로 묶여 오고 남은 횟수가 함께 옵니다. */
@Serializable
data class RetryMissionData(
    val missions: List<RetryMissionGroup> = emptyList(),
    val totalCount: Int = 0,
    /** 지금까지 쓴 재추천 횟수 */
    val retryCount: Int = 0,
)

@Serializable
data class RetryMissionGroup(
    val memberInterestId: Int = 0,
    val data: List<RetryMissionItem> = emptyList(),
)

/** 추천 미션 한 건. 목록 조회와 달리 식별자 필드명이 snake_case 입니다. */
@Serializable
data class RetryMissionItem(
    @SerialName("member_mission_id") val memberMissionId: Int,
    val content: String,
    val missionDescription: String? = null,
    val difficulty: Int = 0,
    val expEarned: Int = 0,
)
