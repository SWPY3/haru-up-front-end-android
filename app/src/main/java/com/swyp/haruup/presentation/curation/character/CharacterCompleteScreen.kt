package com.swyp.haruup.presentation.curation.character

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val HORIZONTAL_MARGIN = 20.dp
private val BUBBLE_TOP_MARGIN = 180.dp
private val BUBBLE_WIDTH = 310.dp
private val BUBBLE_HEIGHT = 114.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_BOTTOM_MARGIN = 10.dp
private val CORNER_RADIUS = 16.dp

/**
 * 큐레이션 2단계. iOS 의 CharacterSelectCompleteViewController 에 대응합니다.
 *
 * 화면 이동 없이 말풍선만 두 번 바뀝니다. 인사 → 안내 순입니다.
 */
@Composable
fun CharacterCompleteScreen(
    characterId: Int,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterCompleteViewModel = hiltViewModel(),
) {
    val step by viewModel.step.collectAsStateWithLifecycle()

    CharacterCompleteContent(
        mate = CharacterMate.entries.firstOrNull { it.id == characterId } ?: CharacterMate.HARU,
        step = step,
        onNextClick = { if (viewModel.onNextClick()) onFinished() },
        modifier = modifier,
    )
}

@Composable
private fun CharacterCompleteContent(
    mate: CharacterMate,
    step: CompleteStep,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val style = mate.completeStyle()

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(R.drawable.background_gradation),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(BUBBLE_TOP_MARGIN))

            Crossfade(targetState = step, label = "speech_bubble") { current ->
                Image(
                    painter = painterResource(
                        when (current) {
                            CompleteStep.WELCOME -> mate.welcomeBubbleRes
                            CompleteStep.GUIDE -> R.drawable.text_box_guide
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(BUBBLE_WIDTH)
                        .height(BUBBLE_HEIGHT),
                )
            }

            Spacer(Modifier.height(style.animationTopMargin))

            CharacterAnimation(mate = mate, style = style)

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNextClick,
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
                    text = when (step) {
                        CompleteStep.WELCOME -> "다음"
                        CompleteStep.GUIDE -> "시작하기"
                    },
                    style = HaruUpType.subtitle2,
                )
            }

            Spacer(Modifier.height(BUTTON_BOTTOM_MARGIN))
        }
    }
}

@Composable
private fun CharacterAnimation(mate: CharacterMate, style: CompleteStyle) {
    Box(
        modifier = Modifier.size(style.animationSize),
        contentAlignment = Alignment.BottomCenter,
    ) {
        // 그림자를 먼저 그려 캐릭터 뒤에 둡니다.
        Image(
            painter = painterResource(R.drawable.character_shadow),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.offset(y = style.shadowOffsetY),
        )

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(mate.animationRes))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever,
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * 캐릭터마다 애니메이션 비율이 달라 크기와 그림자 위치를 따로 잡습니다.
 * iOS 주석에 있는 계산을 그대로 옮겼습니다.
 *
 * 하루는 정사각형 안에 여백이 있어 260 크기에 그림자를 30 올리고,
 * 나루는 여백 없이 꽉 차서 174 크기에 그림자를 15 내립니다.
 * 나루의 위 여백 63 은 하루와 중심 Y 를 맞추기 위한 값입니다. (20 + 260/2 - 174/2)
 */
private data class CompleteStyle(
    val animationSize: Dp,
    val animationTopMargin: Dp,
    val shadowOffsetY: Dp,
)

private fun CharacterMate.completeStyle(): CompleteStyle = when (this) {
    CharacterMate.HARU -> CompleteStyle(
        animationSize = 260.dp,
        animationTopMargin = 20.dp,
        shadowOffsetY = (-30).dp,
    )

    CharacterMate.NARU -> CompleteStyle(
        animationSize = 174.dp,
        animationTopMargin = 63.dp,
        shadowOffsetY = 15.dp,
    )
}

private val CharacterMate.welcomeBubbleRes: Int
    get() = when (this) {
        CharacterMate.HARU -> R.drawable.text_box_welcome_haru
        CharacterMate.NARU -> R.drawable.text_box_welcome_naru
    }

private val CharacterMate.animationRes: Int
    get() = when (this) {
        CharacterMate.HARU -> R.raw.haru_animation
        CharacterMate.NARU -> R.raw.naru_animation
    }

@Preview(showBackground = true, device = "id:pixel_7", name = "하루 · 인사")
@Composable
private fun CharacterCompleteHaruPreview() {
    HaruUpTheme {
        CharacterCompleteContent(CharacterMate.HARU, CompleteStep.WELCOME, {})
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "나루 · 안내")
@Composable
private fun CharacterCompleteNaruPreview() {
    HaruUpTheme {
        CharacterCompleteContent(CharacterMate.NARU, CompleteStep.GUIDE, {})
    }
}
