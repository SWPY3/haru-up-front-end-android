package com.swyp.haruup.network.auth

import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.network.service.TokenReissueService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 만료된 accessToken 을 refreshToken 으로 다시 받습니다.
 *
 * 여러 요청이 한꺼번에 만료되어도 재발급은 한 번만 합니다.
 * 서버가 재발급할 때 이전 refreshToken 을 폐기하므로, 두 번 보내면 두 번째는 실패하고
 * 멀쩡한 사용자가 로그아웃되기 때문입니다.
 */
@Singleton
class TokenRefresher @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val reissueService: TokenReissueService,
    private val sessionEvents: SessionEvents,
) {

    private val mutex = Mutex()

    /**
     * @param failedAccessToken 401/403 을 받은 요청에 실었던 토큰
     * @return 새 accessToken. 다시 시도할 수 없으면 null 입니다.
     *
     * - 그 사이 다른 요청이 이미 재발급했다면 그 토큰을 돌려줍니다.
     * - 서버가 refreshToken 을 받아 주지 않으면 토큰을 지우고 다시 로그인하도록 알립니다.
     * - 통신이 끊긴 경우에는 토큰을 남겨 둡니다. 잠깐 끊겼다고 로그아웃되면 안 되기 때문입니다.
     */
    suspend fun refresh(failedAccessToken: String): String? = mutex.withLock {
        val current = tokenStorage.getAccessToken()
        if (!current.isNullOrBlank() && current != failedAccessToken) return current

        val refreshToken = tokenStorage.getRefreshToken()
        if (refreshToken.isNullOrBlank()) return expire()

        val response = try {
            reissueService.reissue(refreshToken)
        } catch (e: HttpException) {
            return expire()
        } catch (e: IOException) {
            return null
        }

        val tokens = response.data?.takeIf { response.success } ?: return expire()

        tokenStorage.saveTokens(tokens.accessToken, tokens.refreshToken)
        tokens.accessToken
    }

    private suspend fun expire(): String? {
        tokenStorage.clearTokens()
        sessionEvents.notifyExpired()
        return null
    }
}
