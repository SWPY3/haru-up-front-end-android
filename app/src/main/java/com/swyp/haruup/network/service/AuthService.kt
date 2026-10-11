package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.ProfileData
import com.swyp.haruup.data.model.SnsLoginRequest
import com.swyp.haruup.data.model.SnsLoginResponse
import com.swyp.haruup.data.model.WithdrawRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * iOS 의 AuthService.swift 에 대응합니다.
 * 나머지 Service 도 동일한 형태로 추가합니다.
 */
interface AuthService {

    @POST(ApiPath.Auth.SNS_LOGIN)
    suspend fun snsLogin(@Body request: SnsLoginRequest): ApiResponse<SnsLoginResponse>

    /**
     * 서버가 jwt-token 헤더로 받은 refreshToken 을 폐기합니다. 헤더가 없으면 400 입니다.
     * data 에는 안내 문구가 문자열로 옵니다.
     */
    @POST(ApiPath.Auth.LOGOUT)
    suspend fun logout(@Header("jwt-token") refreshToken: String): ApiResponse<String>

    /**
     * 누가 탈퇴하는지는 Authorization 헤더로 압니다. body 는 반드시 보내야 합니다.
     * data 에는 안내 문구가 문자열로 옵니다.
     */
    @POST(ApiPath.Auth.WITHDRAW)
    suspend fun withdraw(@Body request: WithdrawRequest): ApiResponse<String>

    @GET(ApiPath.Profile.PROFILE)
    suspend fun profile(): ApiResponse<ProfileData>
}
