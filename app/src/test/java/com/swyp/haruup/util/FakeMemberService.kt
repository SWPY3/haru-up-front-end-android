package com.swyp.haruup.util

import com.swyp.haruup.data.model.HomeMemberInfoData
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.MemberService

/** 테스트용 MemberService 입니다. */
class FakeMemberService(
    var members: List<HomeMemberInfoData> = emptyList(),
    var shouldFail: Boolean = false,
) : MemberService {

    var callCount = 0
        private set

    override suspend fun homeMemberInfo(): ApiResponse<List<HomeMemberInfoData>> {
        callCount++

        return if (shouldFail) {
            ApiResponse(success = false, data = null)
        } else {
            ApiResponse(success = true, data = members)
        }
    }
}
