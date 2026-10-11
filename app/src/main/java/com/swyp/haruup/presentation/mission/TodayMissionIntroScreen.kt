package com.swyp.haruup.presentation.mission

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val H_MARGIN = 20.dp
private val TITLE_TOP = 169.dp
private val TITLE_TO_SUBTITLE = 20.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_BOTTOM = 20.dp
private val CORNER_RADIUS = 16.dp

/** iOS 는 그라데이션 뷰를 alpha 0.88 로 겹칩니다. */
private const val GRADIENT_ALPHA = 0.88f
private const val CHARACTER_FADE_MILLIS = 500
private const val TEXT_FADE_MILLIS = 300

/**
 * 홈에서 미션을 처음 고르러 온 날 한 번 보여 주는 소개 화면.
 * iOS 의 TodayMissionIntroViewController 에 대응합니다.
 *
 * 캐릭터가 먼저 나타나고, 이어서 문구와 버튼이 나타납니다. (iOS 와 같은 순서와 시간)
 */
@Composable
fun TodayMissionIntroScreen(
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val characterAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        characterAlpha.animateTo(1f, tween(CHARACTER_FADE_MILLIS, easing = FastOutSlowInEasing))
        textAlpha.animateTo(1f, tween(TEXT_FADE_MILLIS, easing = FastOutSlowInEasing))
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Background.Intro),
    ) {
        // 화면 위쪽 절반에만 파란 빛이 번집니다.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight / 2)
                .alpha(GRADIENT_ALPHA)
                .background(
                    Brush.verticalGradient(
                        listOf(HaruUpColor.Gradient.IntroStart, HaruUpColor.Gradient.IntroEnd)
                    )
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(TITLE_TOP))

            Text(
                text = "반가워요!",
                style = HaruUpType.head2,
                color = HaruUpColor.AppWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha.value),
            )

            Spacer(Modifier.height(TITLE_TO_SUBTITLE))

            Text(
                text = "AI가 추천해준 오늘의 미션들을\n선택하러 가볼까요?",
                style = HaruUpType.title3,
                color = HaruUpColor.AppWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha.value),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.character_today_intro),
                    contentDescription = null,
                    modifier = Modifier.alpha(characterAlpha.value),
                )
            }

            Button(
                onClick = onConfirm,
                // 문구가 다 나타나기 전에는 누를 수 없습니다. 보이지 않는 버튼이 눌리면 이상하기 때문입니다.
                enabled = textAlpha.value > 0f,
                shape = RoundedCornerShape(CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HaruUpColor.Cta,
                    contentColor = HaruUpColor.AppWhite,
                    disabledContainerColor = HaruUpColor.Cta,
                    disabledContentColor = HaruUpColor.AppWhite,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = H_MARGIN)
                    .height(BUTTON_HEIGHT)
                    .alpha(textAlpha.value),
            ) {
                Text(text = "좋아요!", style = HaruUpType.subtitle2)
            }

            Spacer(Modifier.height(BUTTON_BOTTOM))
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun TodayMissionIntroPreview() {
    HaruUpTheme {
        TodayMissionIntroScreen(onConfirm = {})
    }
}
