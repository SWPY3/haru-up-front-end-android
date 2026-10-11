package com.swyp.haruup.util

import com.swyp.haruup.data.model.TokenReissueResponse
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.TokenReissueService

/**
 * 테스트용 TokenReissueService 입니다.
 * [respond] 를 바꿔 성공, 서버 거절, 통신 끊김을 만듭니다.
 */
class FakeTokenReissueService(
    var respond: suspend (refreshToken: String) -> ApiResponse<TokenReissueResponse> = {
        ApiResponse(success = true, data = TokenReissueResponse("new-access", "new-refresh"))
    },
) : TokenReissueService {

    val requests = mutableListOf<String>()

    override suspend fun reissue(refreshToken: String): ApiResponse<TokenReissueResponse> {
        requests += refreshToken
        return respond(refreshToken)
    }
}
