package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/**
 * 관심사 계층의 한 항목. iOS 의 InterestData 에 대응합니다.
 * 관심사 / 세부 관심사 / 목표가 모두 같은 모양으로 내려옵니다.
 */
@Serializable
data class InterestData(
    val id: Int,
    val name: String,
    val parentId: Int? = null,
)

/** GET /api/interests/data 응답 */
@Serializable
data class InterestListResponse(
    val interests: List<InterestData> = emptyList(),
    val totalCount: Int = 0,
)

/**
 * 회원이 고른 관심사. iOS 의 MemberInterestDTO 에 대응합니다.
 *
 * directFullPath 는 [관심사, 세부 관심사, 목표] 세 칸짜리 경로입니다.
 * 목표가 없는 분류는 마지막 칸이 빈 문자열로 옵니다.
 */
@Serializable
data class MemberInterest(
    val memberInterestId: Int,
    val memberId: Int = 0,
    val interestId: Int = 0,
    val directFullPath: List<String> = emptyList(),
)

/** GET /api/interests/member 응답 */
@Serializable
data class MemberInterestResponse(
    val interests: List<MemberInterest> = emptyList(),
    val totalCount: Int = 0,
)

/** PUT /api/interests/member/{memberInterestId} 요청 body */
@Serializable
data class UpdateMemberInterestRequest(
    val interestId: Int,
    val directFullPath: List<String>,
)
