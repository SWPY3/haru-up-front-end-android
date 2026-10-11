package com.swyp.haruup.data.social

import android.content.Context
import com.swyp.haruup.data.model.SocialLoginProvider

/**
 * SNS 로그인 창을 띄우고 사용자 정보를 받아옵니다.
 *
 * 서버에 보낼 값만 돌려주고, 그 뒤의 처리는 [com.swyp.haruup.presentation.login.LoginViewModel] 이 합니다.
 * SDK 를 직접 쓰지 않는 자리를 만들어 두어 ViewModel 을 테스트할 수 있게 했습니다.
 */
interface SocialLoginClient {

    /** 이 수단을 쓸 수 있는지. 앱 키가 없으면 false 입니다. */
    fun isAvailable(provider: SocialLoginProvider): Boolean

    /**
     * 로그인 창을 띄웁니다. 화면을 띄워야 해서 Activity [context] 가 필요합니다.
     *
     * @return 사용자가 취소하거나 실패하면 null
     */
    suspend fun login(context: Context, provider: SocialLoginProvider): SocialAccount?
}

/** SNS 가 알려 준 사용자입니다. 서버는 이 값으로 회원을 찾거나 만듭니다. */
data class SocialAccount(
    val provider: SocialLoginProvider,
    /** SNS 쪽 사용자 식별자 */
    val snsId: String,
    val email: String = "",
    val name: String = "",
)
