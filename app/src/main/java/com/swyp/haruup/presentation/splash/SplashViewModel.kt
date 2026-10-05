package com.swyp.haruup.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.local.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.system.measureTimeMillis

/** 스플래시가 끝난 뒤 어디로 갈지입니다. iOS 의 SplashResult 와 같습니다. */
enum class SplashResult {
    /** 로그인부터 */
    NEED_LOGIN,

    /** 로그인은 되어 있지만 큐레이션을 마치지 않음 → 약관 동의부터 */
    ONBOARDING_REQUIRED,

    /** 바로 메인 탭으로 */
    ONBOARDING_COMPLETED,
}

/**
 * 스플래시. iOS 의 SplashViewModel 에 대응합니다.
 *
 * 저장된 토큰만 보고 바로 결정합니다. 서버에 묻지 않습니다.
 * 판단이 빨리 끝나도 로고가 깜빡이지 않도록 최소 [MIN_DISPLAY_MILLIS] 는 머무릅니다.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _result = MutableStateFlow<SplashResult?>(null)
    val result: StateFlow<SplashResult?> = _result.asStateFlow()

    init {
        decide()
    }

    private fun decide() {
        viewModelScope.launch {
            lateinit var result: SplashResult

            val elapsed = measureTimeMillis {
                result = decide(
                    isLoggedIn = tokenStorage.isLoggedIn(),
                    isOnboardingCompleted = tokenStorage.isOnboardingCompleted(),
                )
            }

            delay((MIN_DISPLAY_MILLIS - elapsed).coerceAtLeast(0))
            _result.value = result
        }
    }

    companion object {
        /** 로고가 최소한 이만큼은 보이도록 합니다. (iOS 와 같은 1초) */
        const val MIN_DISPLAY_MILLIS = 1_000L

        /**
         * 어디로 보낼지 정합니다.
         *
         * 로그인이 되어 있어도 큐레이션을 마치지 않았으면 약관 동의부터 다시 시작합니다.
         * 큐레이션을 거치지 않으면 홈에 보여 줄 미션이 없기 때문입니다.
         */
        fun decide(isLoggedIn: Boolean, isOnboardingCompleted: Boolean): SplashResult = when {
            !isLoggedIn -> SplashResult.NEED_LOGIN
            isOnboardingCompleted -> SplashResult.ONBOARDING_COMPLETED
            else -> SplashResult.ONBOARDING_REQUIRED
        }
    }
}
