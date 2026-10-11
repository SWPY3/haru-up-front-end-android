package com.swyp.haruup.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * iOS 의 Resources/Assets.xcassets/color 와 1:1 대응합니다.
 * iOS 앱이 라이트 모드 전용이라 다크 모드 변형은 두지 않습니다.
 *
 * 이름은 iOS 에셋명을 그대로 따랐습니다. 화면을 이관할 때 에셋명으로 검색하면 바로 찾을 수 있습니다.
 */
object HaruUpColor {

    // ---- Base ----
    val AppBlack = Color(0xFF121212)
    val AppWhite = Color(0xFFFFFFFF)

    /** CTA 버튼. primary_blue_700 과 같은 값입니다. */
    val Cta = Color(0xFF2D6CFE)

    // ---- Neutral ----
    val Neutral10 = Color(0xFFF3F4F8)
    val Neutral50 = Color(0xFFECEDF3)
    val Neutral100 = Color(0xFFE0E4EC)
    val Neutral200 = Color(0xFFCDD3DF)
    val Neutral300 = Color(0xFFB8C0D1)
    val Neutral400 = Color(0xFFA6B0C4)
    val Neutral500 = Color(0xFF95A0B8)
    val Neutral600 = Color(0xFF8590A5)
    val Neutral700 = Color(0xFF727A8C)
    val Neutral800 = Color(0xFF616775)
    val Neutral900 = Color(0xFF4C515B)
    val Neutral1000 = Color(0xFF353A43)

    // ---- Primary Blue ----
    /** iOS 에서 primary_blue_50 과 100 이 같은 값입니다. */
    val PrimaryBlue50 = Color(0xFFD5E2FF)
    val PrimaryBlue100 = Color(0xFFD5E2FF)
    val PrimaryBlue200 = Color(0xFFC0D3FF)
    val PrimaryBlue300 = Color(0xFFABC4FF)
    val PrimaryBlue400 = Color(0xFF96B5FE)
    val PrimaryBlue500 = Color(0xFF81A7FE)
    val PrimaryBlue600 = Color(0xFF5789FE)
    val PrimaryBlue700 = Color(0xFF2D6CFE)
    val PrimaryBlue800 = Color(0xFF2456CB)
    val PrimaryBlue900 = Color(0xFF1B4198)

    // ---- Secondary ----
    val SecondaryMint100 = Color(0xFFDEF9F6)
    val SecondaryMint200 = Color(0xFF34D1BF)
    val SecondaryOrange100 = Color(0xFFFFF1E0)
    val SecondaryOrange200 = Color(0xFFFCA538)
    val SecondaryRed100 = Color(0xFFFFDAD9)
    val SecondaryRed200 = Color(0xFFE53935)

    // ---- Background ----
    object Background {
        /** 바텀시트 딤 처리. alpha 0.8 */
        val BottomSheet = Color(0xCC121212)

        /** 인트로 배경. alpha 0.98 */
        val Intro = Color(0xFA353A43)

        val Kakao = Color(0xFFFEE500)
        val Naver = Color(0xFF03C75A)
    }

    // ---- Calendar ----
    object Calendar {
        /** alpha 0.9 */
        val DayWhite = Color(0xE6FFFFFF)

        /** alpha 0.5 */
        val Selected = Color(0x80B8C0D1)
    }

    // ---- Gradient ----
    // iOS 의 linear 그룹입니다. Compose 에서는 Brush.verticalGradient 등으로 조합해 사용합니다.
    object Gradient {
        val CalendarPoint = Color(0xFF96B5FE)

        val IntroStart = Color(0xFF5789FE)

        /** alpha 0.01 — 사실상 투명하게 사라지는 끝점입니다. */
        val IntroEnd = Color(0x035789FE)

        val OnboardingStart = Color(0xFFEFF2FE)
        val OnboardingEnd = Color(0xFFFFFFFF)

        val SplashStart = Color(0xFF8BAEFF)
        val SplashEnd = Color(0xFF1359FE)
    }

    // ---- Shadow ----
    // Compose 에는 색상 있는 그림자 API 가 없습니다.
    // Modifier.shadow(ambientColor =, spotColor =) 로 지정하거나 별도 구현이 필요합니다.
    object Shadow {
        /** alpha 0.14 */
        val Bubble = Color(0x240063CC)

        /** alpha 0.12 */
        val CalendarPoint = Color(0x1F2D6CFE)

        /** alpha 0.08 */
        val TabBar = Color(0x1495A0B8)
    }
}
