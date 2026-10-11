package com.swyp.haruup.util

import com.swyp.haruup.data.model.ProfileData
import com.swyp.haruup.data.model.SnsLoginRequest
import com.swyp.haruup.data.model.SnsLoginResponse
import com.swyp.haruup.data.model.WithdrawRequest
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.AuthService

/** 테스트용 AuthService 입니다. 로그아웃·탈퇴 요청을 기록합니다. */
class FakeAuthService(
    var shouldFail: Boolean = false,
) : AuthService {

    val logoutTokens = mutableListOf<String>()
    val withdrawRequests = mutableListOf<WithdrawRequest>()

    override suspend fun snsLogin(request: SnsLoginRequest): ApiResponse<SnsLoginResponse> =
        error("쓰지 않는 API 입니다.")

    override suspend fun logout(refreshToken: String): ApiResponse<String> {
        logoutTokens += refreshToken
        return ApiResponse(success = !shouldFail, data = "로그아웃 되었습니다.")
    }

    override suspend fun withdraw(request: WithdrawRequest): ApiResponse<String> {
        withdrawRequests += request
        return ApiResponse(success = !shouldFail, data = "회원 탈퇴가 성공적으로 처리되었습니다.")
    }

    override suspend fun profile(): ApiResponse<ProfileData> = ApiResponse(success = false)
}
