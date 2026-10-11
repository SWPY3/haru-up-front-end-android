package com.swyp.haruup.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.swyp.haruup.data.model.WithdrawRequest
import com.swyp.haruup.di.NetworkModule
import com.swyp.haruup.network.service.AuthService
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

/**
 * 로그아웃·탈퇴 요청이 서버가 요구하는 모양으로 나가는지 실제 직렬화로 확인합니다.
 *
 * 서버는 로그아웃에 jwt-token 헤더를, 탈퇴에 body 를 필수로 받아 빠지면 400 을 줍니다.
 * 응답 data 는 문자열이라 Unit 으로 받으면 파싱이 깨집니다.
 */
class AuthServiceRequestTest {

    private lateinit var server: MockWebServer
    private lateinit var service: AuthService

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(NetworkModule.provideJson().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun okWithMessage(message: String) =
        MockResponse().setBody("""{"success":true,"data":"$message"}""")

    @Test
    fun `로그아웃은 jwt-token 헤더에 refreshToken 을 싣고 문자열 응답을 읽는다`() = runTest {
        server.enqueue(okWithMessage("로그아웃 되었습니다."))

        val response = service.logout("refresh-token")

        val request = server.takeRequest()
        assertEquals("/api/member/auth/logout", request.path)
        assertEquals("refresh-token", request.getHeader("jwt-token"))
        assertTrue(response.success)
        assertEquals("로그아웃 되었습니다.", response.data)
    }

    @Test
    fun `탈퇴는 password 를 담은 body 를 보낸다`() = runTest {
        server.enqueue(okWithMessage("회원 탈퇴가 성공적으로 처리되었습니다."))

        val response = service.withdraw(WithdrawRequest())

        val request = server.takeRequest()
        assertEquals("/api/member/account/withdraw", request.path)
        assertEquals("""{"password":""}""", request.body.readUtf8())
        assertTrue(response.success)
    }
}
