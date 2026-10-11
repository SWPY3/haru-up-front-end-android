package com.swyp.haruup.network

import com.swyp.haruup.data.model.TokenReissueResponse
import com.swyp.haruup.network.auth.SessionEvents
import com.swyp.haruup.network.auth.TokenRefresher
import com.swyp.haruup.network.interceptor.AuthInterceptor
import com.swyp.haruup.util.FakeTokenReissueService
import com.swyp.haruup.util.FakeTokenStorage
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * 만료된 토큰으로 거절당했을 때 한 번 재발급받아 다시 보내는지 실제 HTTP 로 확인합니다.
 */
class AuthInterceptorTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun client(
        storage: FakeTokenStorage,
        service: FakeTokenReissueService = FakeTokenReissueService(),
    ): OkHttpClient {
        val refresher = TokenRefresher(storage, service, SessionEvents())
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(storage, refresher))
            .build()
    }

    private fun OkHttpClient.get(): Int =
        newCall(Request.Builder().url(server.url("/api/member/mission")).build())
            .execute()
            .use { it.code }

    @Test
    fun `만료로 403 을 받으면 재발급받은 토큰으로 다시 보낸다`() {
        server.enqueue(MockResponse().setResponseCode(403))
        server.enqueue(MockResponse().setResponseCode(200))
        val storage = FakeTokenStorage(accessToken = "old-access", refreshToken = "old-refresh")

        val code = client(storage).get()

        assertEquals(200, code)
        assertEquals("Bearer old-access", server.takeRequest().getHeader("Authorization"))
        assertEquals("Bearer new-access", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `401 도 같은 방식으로 처리한다`() {
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(200))
        val storage = FakeTokenStorage(accessToken = "old-access", refreshToken = "old-refresh")

        assertEquals(200, client(storage).get())
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `토큰 없이 보낸 요청은 다시 보내지 않는다`() {
        server.enqueue(MockResponse().setResponseCode(403))
        val service = FakeTokenReissueService()

        val code = client(FakeTokenStorage(), service).get()

        assertEquals(403, code)
        assertEquals(1, server.requestCount)
        assertEquals(0, service.requests.size)
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `재발급이 실패하면 원래 응답을 돌려주고 다시 보내지 않는다`() {
        server.enqueue(MockResponse().setResponseCode(403))
        val storage = FakeTokenStorage(accessToken = "old-access", refreshToken = "old-refresh")
        val service = FakeTokenReissueService(respond = { ApiResponse<TokenReissueResponse>(success = false) })

        val code = client(storage, service).get()

        assertEquals(403, code)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `다시 보낸 요청도 거절되면 더 시도하지 않는다`() {
        server.enqueue(MockResponse().setResponseCode(403))
        server.enqueue(MockResponse().setResponseCode(403))
        val storage = FakeTokenStorage(accessToken = "old-access", refreshToken = "old-refresh")
        val service = FakeTokenReissueService()

        val code = client(storage, service).get()

        assertEquals(403, code)
        assertEquals(2, server.requestCount)
        assertEquals(1, service.requests.size)
    }
}
