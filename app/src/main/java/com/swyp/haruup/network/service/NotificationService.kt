package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.RegisterPushTokenRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.POST

/** iOS 의 NetworkDefine.NotificationAPI 에 대응합니다. */
interface NotificationService {

    /** data 는 쓰지 않습니다. 서버가 빈 객체를 주지만 모양에 기대지 않으려고 JsonElement 로 받습니다. */
    @POST(ApiPath.Notification.REGISTER_TOKEN)
    suspend fun registerToken(@Body request: RegisterPushTokenRequest): ApiResponse<JsonElement>
}
