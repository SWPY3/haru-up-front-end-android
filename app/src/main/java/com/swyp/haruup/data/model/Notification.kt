package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/** POST /api/notifications/token 요청 body. iOS PushNotificationService 가 보내는 값과 같습니다. */
@Serializable
data class RegisterPushTokenRequest(
    val memberId: Long,
    /** 기기를 구분하는 값. 같은 기기에서 토큰이 바뀌면 서버가 이 값으로 덮어씁니다. */
    val deviceId: String,
    val platform: String = PLATFORM,
    val token: String,
) {
    companion object {
        /** 서버 PushPlatform enum 값 */
        const val PLATFORM = "ANDROID"
    }
}
