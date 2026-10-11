package com.swyp.haruup.data.push

import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.data.model.RegisterPushTokenRequest
import com.swyp.haruup.network.service.NotificationService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 푸시 토큰을 서버에 등록합니다. iOS 의 PushNotificationService 중 토큰 등록 부분에 대응합니다.
 *
 * 두 때에 보냅니다. (iOS 와 같은 규칙)
 * - 로그인 직후: 토큰이 같아도 항상 보냅니다. 다른 계정으로 로그인했을 수 있기 때문입니다.
 * - 토큰이 새로 발급됐을 때: 로그인되어 있고, 마지막으로 보낸 것과 다를 때만 보냅니다.
 *
 * 실패해도 아무것도 막지 않습니다. 푸시를 못 받는 것보다 로그인이 막히는 쪽이 훨씬 나쁘기 때문입니다.
 */
@Singleton
class PushTokenRegistrar @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val notificationService: NotificationService,
    private val tokenSource: PushTokenSource,
) {

    /** 화면이 닫혀도 등록은 끝까지 하도록 화면과 따로 돕니다. */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** 로그인 직후에 부릅니다. 기다리지 않고 바로 돌아옵니다. */
    fun registerAfterLoginInBackground() {
        scope.launch { registerAfterLogin() }
    }

    suspend fun registerAfterLogin() {
        val token = tokenSource.currentToken() ?: return
        register(token)
    }

    /** Firebase 가 새 토큰을 줬을 때 부릅니다. */
    suspend fun onNewToken(token: String) {
        if (!tokenStorage.isLoggedIn()) return
        if (tokenStorage.getSentPushToken() == token) return
        register(token)
    }

    private suspend fun register(token: String) {
        val memberId = tokenStorage.getMemberId()?.toLongOrNull() ?: return

        val request = RegisterPushTokenRequest(
            memberId = memberId,
            deviceId = tokenSource.deviceId(),
            token = token,
        )

        // 실패하면 보낸 토큰으로 기록하지 않습니다. 다음 로그인이나 토큰 갱신 때 다시 보냅니다.
        val isSaved = runCatching { notificationService.registerToken(request) }
            .getOrNull()
            ?.success == true

        if (isSaved) tokenStorage.setSentPushToken(token)
    }
}
