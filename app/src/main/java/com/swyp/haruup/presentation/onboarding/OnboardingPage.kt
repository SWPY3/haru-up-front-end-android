package com.swyp.haruup.presentation.onboarding

import androidx.annotation.DrawableRes
import com.swyp.haruup.R

/**
 * iOS 의 OnboardingPageModel 에 대응합니다.
 *
 * @param highlight [title] 안에서 강조 색으로 표시할 부분 문자열
 */
enum class OnboardingPage(
    val title: String,
    val highlight: String,
    val description: String,
    @DrawableRes val imageRes: Int,
) {
    MISSION(
        title = "AI가 당신의 성장목표를 분석해\n맞춤 미션을 추천해줘요",
        highlight = "맞춤 미션",
        description = "현재 내가 도전하기 좋은 5단계의 미션을 추천해요.",
        imageRes = R.drawable.image_onboarding_1,
    ),
    CHART(
        title = "월간 미션 차트를 참고해서\n미션을 더 쉽게 고르세요.",
        highlight = "월간 미션 차트",
        description = "나와 같은 사람들이 얼마나 선택했는지 참고 하세요.",
        imageRes = R.drawable.image_onboarding_2,
    ),
    CHARACTER(
        title = "미션을 진행하며\n캐릭터와 함께 성장해요",
        highlight = "캐릭터와 함께 성장해요",
        description = "미션을 완료해 획득한 경험치로 캐릭터가 성장해요.",
        imageRes = R.drawable.image_onboarding_3,
    ),
}
