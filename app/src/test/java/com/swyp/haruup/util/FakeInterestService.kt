package com.swyp.haruup.util

import com.swyp.haruup.data.model.InterestListResponse
import com.swyp.haruup.data.model.MemberInterest
import com.swyp.haruup.data.model.MemberInterestResponse
import com.swyp.haruup.data.model.UpdateMemberInterestRequest
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.InterestService

/** 테스트용 InterestService 입니다. 회원이 고른 관심사만 돌려줍니다. */
class FakeInterestService(
    var memberInterestIds: List<Int> = emptyList(),
    var shouldFail: Boolean = false,
) : InterestService {

    override suspend fun interests(parentId: Int?): InterestListResponse = InterestListResponse()

    override suspend fun memberInterests(): MemberInterestResponse {
        if (shouldFail) error("관심사를 받아오지 못했습니다.")

        return MemberInterestResponse(
            interests = memberInterestIds.map { MemberInterest(memberInterestId = it) },
            totalCount = memberInterestIds.size,
        )
    }

    override suspend fun updateMemberInterest(
        memberInterestId: Int,
        request: UpdateMemberInterestRequest,
    ): ApiResponse<String> = ApiResponse(success = true, data = "ok")
}
