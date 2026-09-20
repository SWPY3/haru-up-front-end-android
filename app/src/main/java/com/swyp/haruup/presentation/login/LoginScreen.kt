package com.swyp.haruup.presentation.login

import androidx.compose.runtime.Composable
import com.swyp.haruup.core.component.PlaceholderScreen

/**
 * iOS 의 Presentation/LoginScreen 에 대응합니다.
 * TODO: 카카오 / 네이버 로그인 SDK 연동
 */
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    PlaceholderScreen(
        title = "로그인",
        buttonText = "다음",
        onButtonClick = onLoginSuccess,
    )
}
