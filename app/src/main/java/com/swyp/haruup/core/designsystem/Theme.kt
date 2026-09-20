package com.swyp.haruup.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HaruUpColorScheme = lightColorScheme(
    primary = HaruUpGreen,
    onPrimary = Color.White,
    secondary = HaruUpGreenDark,
    background = HaruUpBackground,
    onBackground = HaruUpTextPrimary,
    surface = HaruUpSurface,
    onSurface = HaruUpTextPrimary,
)

@Composable
fun HaruUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // 현재 iOS 앱이 라이트 모드만 지원하므로 동일하게 맞춥니다.
    MaterialTheme(
        colorScheme = HaruUpColorScheme,
        typography = HaruUpTypography,
        content = content,
    )
}
