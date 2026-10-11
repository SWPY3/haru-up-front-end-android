package com.swyp.haruup.data.social

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.util.NidOAuthCallback
import com.navercorp.nid.profile.domain.vo.NidProfile
import com.navercorp.nid.profile.util.NidProfileCallback
import com.swyp.haruup.BuildConfig
import com.swyp.haruup.data.model.SocialLoginProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * 카카오 / 네이버 SDK 로 로그인합니다. iOS 의 AuthService 소셜 로그인 부분에 대응합니다.
 *
 * 앱 키가 없으면 그 수단은 쓸 수 없다고만 알리고 SDK 를 건드리지 않습니다.
 * 키 없이 초기화하면 SDK 가 터지기 때문입니다.
 */
@Singleton
class KakaoNaverLoginClient @Inject constructor() : SocialLoginClient {

    override fun isAvailable(provider: SocialLoginProvider): Boolean = when (provider) {
        SocialLoginProvider.KAKAO -> BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()
        SocialLoginProvider.NAVER ->
            BuildConfig.NAVER_CLIENT_ID.isNotBlank() && BuildConfig.NAVER_CLIENT_SECRET.isNotBlank()

        // 애플 로그인은 안드로이드에서 쓰지 않습니다.
        SocialLoginProvider.APPLE -> false
    }

    override suspend fun login(context: Context, provider: SocialLoginProvider): SocialAccount? =
        when (provider) {
            SocialLoginProvider.KAKAO -> loginWithKakao(context)
            SocialLoginProvider.NAVER -> loginWithNaver(context)
            SocialLoginProvider.APPLE -> null
        }

    /**
     * 카카오톡 앱이 깔려 있으면 앱으로, 없으면 카카오 계정 웹으로 로그인합니다. (iOS 와 같은 분기)
     */
    private suspend fun loginWithKakao(context: Context): SocialAccount? {
        val succeeded = suspendCancellableCoroutine { continuation ->
            val handle: (OAuthToken?, Throwable?) -> Unit = { token, _ ->
                continuation.resume(token != null)
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context, callback = handle)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = handle)
            }
        }

        if (!succeeded) return null

        return suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.me { user, _ ->
                val snsId = user?.id?.toString()

                continuation.resume(
                    if (snsId == null) {
                        null
                    } else {
                        SocialAccount(
                            provider = SocialLoginProvider.KAKAO,
                            snsId = snsId,
                            email = user.kakaoAccount?.email.orEmpty(),
                            name = user.kakaoAccount?.profile?.nickname.orEmpty(),
                        )
                    }
                )
            }
        }
    }

    private suspend fun loginWithNaver(context: Context): SocialAccount? {
        val succeeded = suspendCancellableCoroutine { continuation ->
            NaverIdLoginSDK.authenticate(
                context,
                object : NidOAuthCallback {
                    override fun onSuccess() = continuation.resume(true)

                    override fun onFailure(message: String, description: String) =
                        continuation.resume(false)
                },
            )
        }

        if (!succeeded) return null

        return suspendCancellableCoroutine { continuation ->
            NidOAuthLogin().callProfileApi(
                object : NidProfileCallback<NidProfile> {
                    override fun onSuccess(result: NidProfile) {
                        val profile = result.profile
                        val snsId = profile?.id

                        continuation.resume(
                            if (snsId.isNullOrBlank()) {
                                null
                            } else {
                                SocialAccount(
                                    provider = SocialLoginProvider.NAVER,
                                    snsId = snsId,
                                    email = profile.email.orEmpty(),
                                    // 네이버는 이름과 별명이 따로 옵니다. 이름을 먼저 씁니다.
                                    name = profile.name ?: profile.nickname.orEmpty(),
                                )
                            }
                        )
                    }

                    override fun onFailure(message: String, description: String) =
                        continuation.resume(null)
                },
            )
        }
    }
}
