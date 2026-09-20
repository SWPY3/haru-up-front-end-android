package com.swyp.haruup.presentation.curation.character

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val HORIZONTAL_MARGIN = 20.dp
private val TITLE_TOP_MARGIN = 100.dp
private val TITLE_TO_CHARACTER = 150.dp
private val CHARACTER_SIZE = 180.dp
private val ARROW_SIZE = 32.dp
private val ARROW_TOP_OFFSET = 110.dp
private val CHARACTER_TO_NAME = 20.dp
private val SHADOW_TO_NAME = 5.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_BOTTOM_MARGIN = 10.dp
private val CORNER_RADIUS = 16.dp

/** 비활성 화살표의 투명도. iOS 와 동일하게 0.2 입니다. */
private const val DISABLED_ARROW_ALPHA = 0.2f

/**
 * 큐레이션 1단계. iOS 의 CharacterSelectViewController 에 대응합니다.
 */
@Composable
fun CharacterSelectScreen(
    onNextClick: (characterId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CharacterSelectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CharacterSelectContent(
        uiState = uiState,
        onPreviousClick = viewModel::onPreviousClick,
        onNextCharacterClick = viewModel::onNextClick,
        onCharacterClick = viewModel::onCharacterClick,
        onConfirmClick = { onNextClick(uiState.selected.id) },
        modifier = modifier,
    )
}

@Composable
private fun CharacterSelectContent(
    uiState: CharacterSelectUiState,
    onPreviousClick: () -> Unit,
    onNextCharacterClick: () -> Unit,
    onCharacterClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            Spacer(Modifier.height(TITLE_TOP_MARGIN))

            Text(
                text = "앞으로 함께 성장할\n메이트를 선택해주세요!",
                style = HaruUpType.onboardingTitle,
                color = HaruUpColor.AppBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN),
            )

            Spacer(Modifier.height(TITLE_TO_CHARACTER))

            // 캐릭터와 좌우 화살표는 같은 줄에 놓입니다.
            // iOS 는 화살표를 캐릭터 상단에서 110 아래에 두므로 여기서도 같은 오프셋을 씁니다.
            Box(modifier = Modifier.fillMaxWidth()) {

                Crossfade(
                    targetState = uiState.selected,
                    label = "character",
                    modifier = Modifier.align(Alignment.TopCenter),
                ) { mate ->
                    Image(
                        painter = painterResource(mate.imageRes),
                        contentDescription = uiState.displayName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(CHARACTER_SIZE)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onCharacterClick,
                            ),
                    )
                }

                ArrowButton(
                    iconRes = R.drawable.ic_chevron_left,
                    contentDescription = "이전 캐릭터",
                    enabled = uiState.canGoPrevious,
                    onClick = onPreviousClick,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = HORIZONTAL_MARGIN, top = ARROW_TOP_OFFSET),
                )

                ArrowButton(
                    iconRes = R.drawable.ic_chevron_right,
                    contentDescription = "다음 캐릭터",
                    enabled = uiState.canGoNext,
                    onClick = onNextCharacterClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = HORIZONTAL_MARGIN, top = ARROW_TOP_OFFSET),
                )
            }

            Spacer(Modifier.height(CHARACTER_TO_NAME - SHADOW_TO_NAME))

            Image(
                painter = painterResource(R.drawable.character_shadow),
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )

            Spacer(Modifier.height(SHADOW_TO_NAME))

            Text(
                text = uiState.displayName,
                style = HaruUpType.title3,
                color = HaruUpColor.AppBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN),
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onConfirmClick,
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
                Text(text = "다음", style = HaruUpType.subtitle2)
            }

            Spacer(Modifier.height(BUTTON_BOTTOM_MARGIN))
        }
    }
}

@Composable
private fun ArrowButton(
    iconRes: Int,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(iconRes),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(ARROW_SIZE)
            .alpha(if (enabled) 1f else DISABLED_ARROW_ALPHA)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    )
}

@Preview(showBackground = true, device = "id:pixel_7", name = "하루 선택")
@Composable
private fun CharacterSelectPreview() {
    HaruUpTheme {
        CharacterSelectContent(
            uiState = CharacterSelectUiState(selected = CharacterMate.HARU),
            onPreviousClick = {}, onNextCharacterClick = {}, onCharacterClick = {}, onConfirmClick = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "나루 선택")
@Composable
private fun CharacterSelectNaruPreview() {
    HaruUpTheme {
        CharacterSelectContent(
            uiState = CharacterSelectUiState(selected = CharacterMate.NARU),
            onPreviousClick = {}, onNextCharacterClick = {}, onCharacterClick = {}, onConfirmClick = {},
        )
    }
}
