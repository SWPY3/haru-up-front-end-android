package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.SnsLoginRequest
import com.swyp.haruup.data.model.SnsLoginResponse
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * iOS 의 AuthService.swift 에 대응합니다.
 * 나머지 Service 도 동일한 형태로 추가합니다.
 */
interface AuthService {

    @POST(ApiPath.Auth.SNS_LOGIN)
    suspend fun snsLogin(@Body request: SnsLoginRequest): ApiResponse<SnsLoginResponse>

    @POST(ApiPath.Auth.LOGOUT)
    suspend fun logout(): ApiResponse<Unit>

    @POST(ApiPath.Auth.WITHDRAW)
    suspend fun withdraw(): ApiResponse<Unit>
}
