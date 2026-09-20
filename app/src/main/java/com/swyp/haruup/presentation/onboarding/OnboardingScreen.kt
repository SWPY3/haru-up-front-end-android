package com.swyp.haruup.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.onboarding.component.OnboardingPageContent
import com.swyp.haruup.presentation.onboarding.component.PageIndicator
import kotlinx.coroutines.launch

private val PAGER_TO_INDICATOR = 14.dp
private val INDICATOR_TO_BUTTON = 28.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_BOTTOM_MARGIN = 10.dp
private val HORIZONTAL_MARGIN = 20.dp
private val CORNER_RADIUS = 16.dp

/**
 * iOS 의 OnboardingViewController 에 대응합니다.
 *
 * 페이지 인덱스는 Compose 의 PagerState 가 들고 있습니다.
 * iOS 는 ViewModel 이 currentPage 를 관리하지만, 여기서는 스와이프와 버튼 이동을
 * PagerState 하나로 다룰 수 있어 별도 ViewModel 을 두지 않았습니다.
 */
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = OnboardingPage.entries
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val isLastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(HaruUpColor.Gradient.OnboardingStart, HaruUpColor.Gradient.OnboardingEnd)
                )
            )
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { index ->
            OnboardingPageContent(page = pages[index])
        }

        Spacer(Modifier.height(PAGER_TO_INDICATOR))

        PageIndicator(pageCount = pages.size, currentPage = pagerState.currentPage)

        Spacer(Modifier.height(INDICATOR_TO_BUTTON))

        Button(
            onClick = {
                if (isLastPage) {
                    onFinished()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            shape = RoundedCornerShape(CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = HaruUpColor.Cta,
                contentColor = HaruUpColor.AppWhite,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HORIZONTAL_MARGIN)
                .height(BUTTON_HEIGHT),
        ) {
            Text(
                text = if (isLastPage) "시작하기" else "다음",
                style = HaruUpType.subtitle2,
            )
        }

        Spacer(Modifier.height(BUTTON_BOTTOM_MARGIN))
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun OnboardingScreenPreview() {
    HaruUpTheme {
        OnboardingScreen(onFinished = {})
    }
}
