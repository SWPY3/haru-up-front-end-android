package com.swyp.haruup.util

import com.swyp.haruup.data.model.RegisterPushTokenRequest
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.NotificationService
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** 테스트용 NotificationService 입니다. 보낸 요청을 기록합니다. */
class FakeNotificationService(
    var shouldFail: Boolean = false,
) : NotificationService {

    val requests = mutableListOf<RegisterPushTokenRequest>()

    override suspend fun registerToken(request: RegisterPushTokenRequest): ApiResponse<JsonElement> {
        requests += request
        if (shouldFail) error("등록하지 못했습니다.")
        return ApiResponse(success = true, data = JsonObject(emptyMap()))
    }
}
