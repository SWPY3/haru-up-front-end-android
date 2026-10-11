package com.swyp.haruup.data.push

import com.swyp.haruup.data.model.RegisterPushTokenRequest
import com.swyp.haruup.util.FakeNotificationService
import com.swyp.haruup.util.FakePushTokenSource
import com.swyp.haruup.util.FakeTokenStorage
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 푸시 토큰을 언제, 무엇을 담아 서버에 보내는지 확인합니다. iOS PushNotificationService 와 같은 규칙입니다.
 */
class PushTokenRegistrarTest {

    private fun loggedIn(sentPushToken: String? = null) = FakeTokenStorage(
        accessToken = "access",
        memberId = "42",
        sentPushToken = sentPushToken,
    )

    @Test
    fun `로그인 직후에는 회원 id 와 기기 정보를 담아 보낸다`() = runTest {
        val storage = loggedIn()
        val service = FakeNotificationService()

        PushTokenRegistrar(storage, service, FakePushTokenSource()).registerAfterLogin()

        assertEquals(
            listOf(RegisterPushTokenRequest(memberId = 42, deviceId = "device-1", platform = "ANDROID", token = "fcm-token")),
            service.requests,
        )
        assertEquals("fcm-token", storage.getSentPushToken())
    }

    @Test
    fun `로그인 직후에는 이미 보낸 토큰이어도 다시 보낸다`() = runTest {
        // 다른 계정으로 로그인했을 수 있어 iOS 도 항상 보냅니다.
        val service = FakeNotificationService()

        PushTokenRegistrar(loggedIn(sentPushToken = "fcm-token"), service, FakePushTokenSource()).registerAfterLogin()

        assertEquals(1, service.requests.size)
    }

    @Test
    fun `Firebase 가 꺼져 있어 토큰이 없으면 보내지 않는다`() = runTest {
        val service = FakeNotificationService()

        PushTokenRegistrar(loggedIn(), service, FakePushTokenSource(token = null)).registerAfterLogin()

        assertTrue(service.requests.isEmpty())
    }

    @Test
    fun `새 토큰은 로그인되어 있고 바뀌었을 때만 보낸다`() = runTest {
        val service = FakeNotificationService()
        val storage = loggedIn(sentPushToken = "old-token")
        val registrar = PushTokenRegistrar(storage, service, FakePushTokenSource())

        registrar.onNewToken("old-token")
        assertTrue(service.requests.isEmpty())

        registrar.onNewToken("new-token")
        assertEquals("new-token", service.requests.single().token)
        assertEquals("new-token", storage.getSentPushToken())
    }

    @Test
    fun `로그인 전에 받은 새 토큰은 보내지 않는다`() = runTest {
        val service = FakeNotificationService()

        PushTokenRegistrar(FakeTokenStorage(), service, FakePushTokenSource()).onNewToken("new-token")

        assertTrue(service.requests.isEmpty())
    }

    @Test
    fun `등록에 실패하면 보낸 토큰으로 기록하지 않는다`() = runTest {
        // 기록해 두면 다음에 토큰이 같다는 이유로 다시 보내지 않게 됩니다.
        val storage = loggedIn()

        PushTokenRegistrar(storage, FakeNotificationService(shouldFail = true), FakePushTokenSource()).registerAfterLogin()

        assertNull(storage.getSentPushToken())
    }
}
