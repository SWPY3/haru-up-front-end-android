package com.swyp.haruup.network

import com.swyp.haruup.data.model.TokenReissueResponse
import com.swyp.haruup.network.auth.SessionEvents
import com.swyp.haruup.network.auth.TokenRefresher
import com.swyp.haruup.util.FakeTokenReissueService
import com.swyp.haruup.util.FakeTokenStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * 재발급이 한 번만 일어나는지, 실패 종류에 따라 로그아웃 여부가 맞게 갈리는지 확인합니다.
 *
 * 서버는 재발급할 때 이전 refreshToken 을 폐기합니다.
 * 두 번 보내면 두 번째가 실패해 멀쩡한 사용자가 로그아웃되므로 그 경로를 고정합니다.
 */
class TokenRefresherTest {

    private fun storage() = FakeTokenStorage(accessToken = "old-access", refreshToken = "old-refresh")

    @Test
    fun `재발급받은 토큰을 둘 다 저장한다`() = runTest {
        val storage = storage()
        val service = FakeTokenReissueService()
        val refresher = TokenRefresher(storage, service, SessionEvents())

        val token = refresher.refresh("old-access")

        assertEquals("new-access", token)
        assertEquals(listOf("old-refresh"), service.requests)
        assertEquals("new-access", storage.getAccessToken())
        // 서버가 이전 refreshToken 을 폐기하므로 새것으로 바꿔 둬야 다음 재발급이 됩니다.
        assertEquals("new-refresh", storage.getRefreshToken())
    }

    @Test
    fun `다른 요청이 이미 재발급했으면 다시 보내지 않는다`() = runTest {
        val storage = FakeTokenStorage(accessToken = "already-new", refreshToken = "new-refresh")
        val service = FakeTokenReissueService()

        val token = TokenRefresher(storage, service, SessionEvents()).refresh("old-access")

        assertEquals("already-new", token)
        assertTrue(service.requests.isEmpty())
    }

    @Test
    fun `동시에 만료되어도 재발급은 한 번만 한다`() = runTest {
        val storage = storage()
        val service = FakeTokenReissueService(respond = {
            delay(100)
            ApiResponse(
                success = true,
                data = TokenReissueResponse("new-access", "new-refresh"),
            )
        })
        val refresher = TokenRefresher(storage, service, SessionEvents())

        val tokens = List(3) { async { refresher.refresh("old-access") } }.awaitAll()

        assertEquals(1, service.requests.size)
        assertEquals(listOf("new-access", "new-access", "new-access"), tokens)
    }

    @Test
    fun `서버가 refreshToken 을 거절하면 토큰을 지우고 로그인을 다시 하게 한다`() = runTest {
        val storage = storage()
        val events = SessionEvents()
        val service = FakeTokenReissueService(respond = {
            throw HttpException(Response.error<Any>(400, "".toResponseBody()))
        })
        val expired = async { events.expired.first() }
        advanceUntilIdle()

        val token = TokenRefresher(storage, service, events).refresh("old-access")
        advanceUntilIdle()

        assertNull(token)
        assertNull(storage.getAccessToken())
        assertNull(storage.getRefreshToken())
        assertTrue(expired.isCompleted)
    }

    @Test
    fun `실패 응답을 받아도 로그인을 다시 하게 한다`() = runTest {
        val storage = storage()
        val service = FakeTokenReissueService(respond = {
            ApiResponse(success = false)
        })

        val token = TokenRefresher(storage, service, SessionEvents()).refresh("old-access")

        assertNull(token)
        assertNull(storage.getRefreshToken())
    }

    @Test
    fun `refreshToken 이 없으면 보내지 않고 로그인을 다시 하게 한다`() = runTest {
        val storage = FakeTokenStorage(accessToken = "old-access")
        val service = FakeTokenReissueService()

        val token = TokenRefresher(storage, service, SessionEvents()).refresh("old-access")

        assertNull(token)
        assertTrue(service.requests.isEmpty())
        assertNull(storage.getAccessToken())
    }

    @Test
    fun `통신이 끊겼으면 토큰을 남겨 둔다`() = runTest {
        val storage = storage()
        val events = SessionEvents()
        var expiredCount = 0
        val job = launch { events.expired.collect { expiredCount++ } }
        advanceUntilIdle()

        val service = FakeTokenReissueService(respond = { throw IOException("끊김") })
        val token = TokenRefresher(storage, service, events).refresh("old-access")
        advanceUntilIdle()

        assertNull(token)
        assertEquals("old-access", storage.getAccessToken())
        assertEquals("old-refresh", storage.getRefreshToken())
        assertEquals(0, expiredCount)
        job.cancel()
    }
}
