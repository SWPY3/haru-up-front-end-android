package com.swyp.haruup.presentation.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.data.model.SnsLoginRequest
import com.swyp.haruup.data.model.SocialLoginProvider
import com.swyp.haruup.data.social.SocialAccount
import com.swyp.haruup.data.social.SocialLoginClient
import com.swyp.haruup.network.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 로그인 뒤 어디로 갈지입니다. 스플래시의 갈래와 같은 기준을 씁니다. */
enum class LoginDestination { ONBOARDING, MAIN_TAB }

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

/**
 * 로그인 화면. iOS 의 LoginViewModel + AuthService 의 소셜 로그인 흐름에 대응합니다.
 *
 * SNS 에서 사용자 정보를 받아 서버에 보내고, 받은 토큰을 저장한 뒤 다음 화면을 정합니다.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val socialLoginClient: SocialLoginClient,
    private val authService: AuthService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onLoginClick(
        context: Context,
        provider: SocialLoginProvider,
        onSuccess: (LoginDestination) -> Unit,
    ) {
        if (_uiState.value.isLoading) return

        if (!socialLoginClient.isAvailable(provider)) {
            _uiState.update { it.copy(errorMessage = notConfiguredMessage(provider)) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val account = runCatching { socialLoginClient.login(context, provider) }.getOrNull()

            if (account == null) {
                // 사용자가 취소한 경우도 여기로 옵니다. 그때는 조용히 지나갑니다.
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val destination = signIn(account)

            if (destination == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = LOGIN_FAILED) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = false) }
            onSuccess(destination)
        }
    }

    /**
     * SNS 사용자 정보를 서버에 보내고 토큰을 저장합니다.
     *
     * @return 다음에 갈 화면. 실패하면 null
     */
    private suspend fun signIn(account: SocialAccount): LoginDestination? {
        val data = runCatching {
            authService.snsLogin(
                SnsLoginRequest(
                    loginType = account.provider.value,
                    snsId = account.snsId,
                    email = account.email,
                    name = account.name,
                )
            )
        }.getOrNull()
            ?.takeIf { it.success }
            ?.data
            ?: return null

        tokenStorage.saveTokens(data.accessToken, data.refreshToken)

        val memberId = data.id.toString()
        tokenStorage.saveMemberId(memberId)

        // 지난번과 다른 계정이면 큐레이션을 다시 시킵니다.
        tokenStorage.clearOnboardingIfDifferentUser(memberId)

        return destinationFor(tokenStorage.isOnboardingCompleted())
    }

    companion object {
        const val LOGIN_FAILED = "로그인에 실패했어요. 잠시 후 다시 시도해주세요."

        /**
         * 로그인 뒤 어디로 갈지 정합니다.
         * 큐레이션을 마치지 않았으면 약관 동의부터 다시 시작합니다. (스플래시와 같은 기준)
         */
        fun destinationFor(onboardingCompleted: Boolean): LoginDestination =
            if (onboardingCompleted) LoginDestination.MAIN_TAB else LoginDestination.ONBOARDING

        fun notConfiguredMessage(provider: SocialLoginProvider): String =
            "${providerLabel(provider)} 로그인이 아직 준비되지 않았어요."

        private fun providerLabel(provider: SocialLoginProvider): String = when (provider) {
            SocialLoginProvider.KAKAO -> "카카오"
            SocialLoginProvider.NAVER -> "네이버"
            SocialLoginProvider.APPLE -> "애플"
        }
    }
}
