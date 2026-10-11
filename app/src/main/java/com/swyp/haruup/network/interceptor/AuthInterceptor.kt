package com.swyp.haruup.network.interceptor

import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.network.auth.TokenRefresher
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * iOS 의 TokenStorageService + Alamofire RequestInterceptor 역할에 대응합니다.
 *
 * 요청마다 accessToken 을 붙이고, 만료로 거절되면 한 번 재발급받아 다시 보냅니다.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val tokenRefresher: TokenRefresher,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = runBlocking { tokenStorage.getAccessToken() }

        val response = chain.proceed(original.withToken(token))

        // 토큰 없이 보낸 요청은 재발급해도 달라질 게 없습니다.
        if (token.isNullOrBlank() || response.code !in EXPIRED_CODES) return response

        val newToken = runBlocking { tokenRefresher.refresh(token) } ?: return response

        // 다시 보내기 전에 앞선 응답을 닫아야 연결이 새지 않습니다.
        response.close()
        return chain.proceed(original.withToken(newToken))
    }

    private fun Request.withToken(token: String?): Request =
        if (token.isNullOrBlank()) {
            this
        } else {
            newBuilder().header("Authorization", "Bearer $token").build()
        }

    companion object {
        /**
         * 서버는 만료된 토큰에 403 을 줍니다. 인증 실패 응답을 따로 정하지 않아
         * Spring Security 기본값이 쓰이기 때문입니다. 401 도 함께 봅니다.
         */
        val EXPIRED_CODES = setOf(401, 403)
    }
}
