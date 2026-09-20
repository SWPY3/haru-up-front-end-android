package com.swyp.haruup.presentation.onboarding.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.onboarding.OnboardingPage

private val TEXT_SPACING = 12.dp
private val TEXT_TO_IMAGE = 18.dp

/**
 * iOS 의 OnboardingPageView 에 대응합니다.
 * 이미지가 하단에 붙고, 그 위로 18 띄워 제목·설명이 놓입니다.
 */
@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(TEXT_SPACING),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = remember(page) { page.highlightedTitle() },
                style = HaruUpType.onboardingTitle,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = page.description,
                style = HaruUpType.body3,
                color = HaruUpColor.Neutral700,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(TEXT_TO_IMAGE))

        Image(
            painter = painterResource(page.imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )
    }
}

/** 제목에서 highlight 부분만 강조 색으로 칠합니다. */
private fun OnboardingPage.highlightedTitle(): AnnotatedString = buildAnnotatedString {
    val start = title.indexOf(highlight)
    if (start < 0) {
        // 강조 문구를 찾지 못하면 기본 색으로만 표시한다.
        withStyle(SpanStyle(color = HaruUpColor.AppBlack)) { append(title) }
        return@buildAnnotatedString
    }

    withStyle(SpanStyle(color = HaruUpColor.AppBlack)) {
        append(title.substring(0, start))
    }
    withStyle(SpanStyle(color = HaruUpColor.Cta)) {
        append(highlight)
    }
    withStyle(SpanStyle(color = HaruUpColor.AppBlack)) {
        append(title.substring(start + highlight.length))
    }
}
