package com.swyp.haruup.util

import com.swyp.haruup.data.push.PushTokenSource

/** 테스트용 PushTokenSource 입니다. token 이 null 이면 Firebase 가 꺼진 상태와 같습니다. */
class FakePushTokenSource(
    var token: String? = "fcm-token",
    private val deviceId: String = "device-1",
) : PushTokenSource {

    override suspend fun currentToken(): String? = token

    override fun deviceId(): String = deviceId
}
