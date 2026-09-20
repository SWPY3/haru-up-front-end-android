package com.swyp.haruup.presentation.agree

import androidx.compose.runtime.Composable
import com.swyp.haruup.core.component.PlaceholderScreen

/** iOS 의 Presentation/Agree 에 대응합니다. */
@Composable
fun AgreeScreen(onAgreed: () -> Unit) {
    PlaceholderScreen(title = "약관 동의", buttonText = "다음", onButtonClick = onAgreed)
}
