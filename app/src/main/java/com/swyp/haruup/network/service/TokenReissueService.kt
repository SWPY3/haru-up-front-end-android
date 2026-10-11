package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.TokenReissueResponse
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 토큰 재발급 전용입니다.
 *
 * AuthInterceptor 를 거치지 않는 클라이언트로 만듭니다.
 * 같은 클라이언트를 쓰면 재발급 요청이 다시 재발급을 부르며 맴돌 수 있기 때문입니다.
 */
interface TokenReissueService {

    @POST(ApiPath.Auth.TOKEN_REISSUE)
    suspend fun reissue(@Header("jwt-token") refreshToken: String): ApiResponse<TokenReissueResponse>
}
