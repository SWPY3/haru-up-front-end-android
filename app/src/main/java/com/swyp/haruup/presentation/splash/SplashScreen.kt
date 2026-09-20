package com.swyp.haruup.presentation.splash

import androidx.compose.runtime.Composable
import com.swyp.haruup.core.component.PlaceholderScreen

/**
 * iOS 의 Presentation/Splash 에 대응합니다.
 * TODO: 저장된 토큰 유효성을 확인해 onLoggedIn / onLoggedOut 중 하나를 호출합니다.
 */
@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
) {
    PlaceholderScreen(
        title = "스플래시",
        buttonText = "로그인 화면으로",
        onButtonClick = onLoggedOut,
    )
}
