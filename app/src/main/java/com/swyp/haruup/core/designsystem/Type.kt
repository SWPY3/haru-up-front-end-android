package com.swyp.haruup.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.swyp.haruup.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_thin, FontWeight.Thin),
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight),
    Font(R.font.pretendard_light, FontWeight.Light),
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold),
    Font(R.font.pretendard_black, FontWeight.Black),
)

/**
 * iOS 는 폰트 크기에 lineHeight 배수(대부분 150%)를 곱해 씁니다.
 * Compose 는 lineHeight 를 sp 절대값으로 받으므로 계산된 값을 넣었습니다.
 *
 * includeFontPadding 을 끄고 trim 을 없애야 iOS 와 줄 간격이 비슷해집니다.
 */
private fun haruUpStyle(
    size: Int,
    weight: FontWeight,
    lineHeightMultiplier: Float = 1.50f,
) = TextStyle(
    fontFamily = Pretendard,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = (size * lineHeightMultiplier).sp,
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

/**
 * iOS 의 Resources/Fonts/Typography.swift 와 1:1 대응합니다.
 * 이름을 그대로 맞췄으므로 화면 이관 시 Typography.body1 → HaruUpType.body1 로 옮기면 됩니다.
 */
object HaruUpType {

    // Head
    val head1 = haruUpStyle(32, FontWeight.Bold)
    val head2 = haruUpStyle(30, FontWeight.Bold, 1.00f)

    // Title
    val title1 = haruUpStyle(24, FontWeight.Bold)
    val title2 = haruUpStyle(24, FontWeight.SemiBold)
    val title3 = haruUpStyle(20, FontWeight.SemiBold)

    // Subtitle
    val subtitle1 = haruUpStyle(18, FontWeight.SemiBold)
    val subtitle2 = haruUpStyle(16, FontWeight.SemiBold)

    // Body
    val body1 = haruUpStyle(16, FontWeight.Medium)
    val body2 = haruUpStyle(14, FontWeight.Bold)
    val body3 = haruUpStyle(14, FontWeight.SemiBold)
    val body4 = haruUpStyle(14, FontWeight.Medium)
    val body5 = haruUpStyle(13, FontWeight.Medium)

    // Footnote
    val footnote = haruUpStyle(13, FontWeight.Normal)

    // Caption
    val caption1 = haruUpStyle(12, FontWeight.SemiBold)
    val caption2 = haruUpStyle(12, FontWeight.Medium)
    val caption3 = haruUpStyle(12, FontWeight.Normal)

    // Level
    val level = haruUpStyle(13, FontWeight.SemiBold)

    // Difficulty / Exp / Retry Button
    val difficulty = haruUpStyle(14, FontWeight.Medium, 1.40f)
    val exp = haruUpStyle(14, FontWeight.Medium, 1.40f)
    val retryButton = haruUpStyle(14, FontWeight.Medium, 1.40f)

    // Calendar
    val calendarWeek = haruUpStyle(13, FontWeight.Medium)
    val calendarDay = haruUpStyle(13, FontWeight.Medium)

    /**
     * 소셜 로그인 버튼.
     * iOS 는 Typography 에 정의하지 않고 systemFont(19, semiBold) 를 직접 지정합니다.
     * 여기서는 앱 전체 일관성을 위해 Pretendard SemiBold 를 씁니다.
     */
    val socialLogin = haruUpStyle(19, FontWeight.SemiBold)

    // Chart
    val yText = haruUpStyle(11, FontWeight.Medium, 1.00f)
    val xText = haruUpStyle(13, FontWeight.Medium, 1.00f)
    val description = haruUpStyle(16, FontWeight.Normal)
}

/**
 * Material3 컴포넌트(Button, NavigationBar 등)가 기본으로 쓰는 타이포그래피입니다.
 * 직접 만드는 화면에서는 HaruUpType 을 쓰고, 이쪽은 기본값 대체용으로만 둡니다.
 */
val HaruUpTypography = Typography(
    headlineLarge = HaruUpType.head1,
    headlineMedium = HaruUpType.title1,
    headlineSmall = HaruUpType.title3,
    titleLarge = HaruUpType.subtitle1,
    titleMedium = HaruUpType.subtitle2,
    titleSmall = HaruUpType.body3,
    bodyLarge = HaruUpType.body1,
    bodyMedium = HaruUpType.body4,
    bodySmall = HaruUpType.body5,
    labelLarge = HaruUpType.body3,
    labelMedium = HaruUpType.caption1,
    labelSmall = HaruUpType.caption2,
)
