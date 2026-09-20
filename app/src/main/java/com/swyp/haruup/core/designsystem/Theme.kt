package com.swyp.haruup.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * iOS 앱이 라이트 모드 전용이라 다크 모드를 받지 않습니다.
 * 색상은 HaruUpColor 를 직접 참조하고, 이 ColorScheme 은 Material3 컴포넌트 기본값 용도입니다.
 */
private val HaruUpColorScheme = lightColorScheme(
    primary = HaruUpColor.PrimaryBlue700,
    onPrimary = HaruUpColor.AppWhite,
    primaryContainer = HaruUpColor.PrimaryBlue100,
    onPrimaryContainer = HaruUpColor.PrimaryBlue900,
    secondary = HaruUpColor.SecondaryMint200,
    onSecondary = HaruUpColor.AppWhite,
    error = HaruUpColor.SecondaryRed200,
    onError = HaruUpColor.AppWhite,
    background = HaruUpColor.AppWhite,
    onBackground = HaruUpColor.AppBlack,
    surface = HaruUpColor.AppWhite,
    onSurface = HaruUpColor.AppBlack,
    surfaceVariant = HaruUpColor.Neutral10,
    onSurfaceVariant = HaruUpColor.Neutral700,
    outline = HaruUpColor.Neutral300,
)

@Composable
fun HaruUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HaruUpColorScheme,
        typography = HaruUpTypography,
        content = content,
    )
}
