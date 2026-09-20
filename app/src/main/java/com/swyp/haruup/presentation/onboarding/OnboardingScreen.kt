package com.swyp.haruup.presentation.onboarding

import androidx.compose.runtime.Composable
import com.swyp.haruup.core.component.PlaceholderScreen

/** iOS 의 Presentation/Onboarding 에 대응합니다. */
@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    PlaceholderScreen(title = "온보딩", buttonText = "다음", onButtonClick = onFinished)
}
