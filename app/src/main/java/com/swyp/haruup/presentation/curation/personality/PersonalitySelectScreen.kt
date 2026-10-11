package com.swyp.haruup.presentation.curation.personality

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.swyp.haruup.core.component.SelectButton
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val HORIZONTAL_MARGIN = 20.dp
private val TITLE_TOP_MARGIN = 100.dp
private val TITLE_TO_SUBTITLE = 12.dp
private val SUBTITLE_TO_OPTIONS = 48.dp
private val OPTION_SPACING = 12.dp
private val OPTION_HEIGHT = 64.dp
private val OPTIONS_TO_INDICATOR = 24.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_BOTTOM_MARGIN = 10.dp
private val CORNER_RADIUS = 16.dp

/** 비활성 다음 버튼의 투명도. iOS 와 동일하게 0.4 입니다. */
private const val DISABLED_BUTTON_ALPHA = 0.4f

/**
 * 큐레이션 3단계. iOS 의 PersonalitySelectViewController 에 대응합니다.
 */
@Composable
fun PersonalitySelectScreen(
    onCompleted: (personalityCode: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalitySelectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PersonalitySelectContent(
        uiState = uiState,
        onPersonalitySelected = viewModel::onPersonalitySelected,
        onNextClick = { viewModel.onNextClick(onCompleted) },
        modifier = modifier,
    )
}

@Composable
private fun PersonalitySelectContent(
    uiState: PersonalitySelectUiState,
    onPersonalitySelected: (Int) -> Unit,
    onNextClick: () -> Unit,
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
                text = "어떤 방식으로\n도와드릴까요?",
                style = HaruUpType.onboardingTitle,
                color = HaruUpColor.AppBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN),
            )

            Spacer(Modifier.height(TITLE_TO_SUBTITLE))

            Text(
                text = "선택한 성격에 맞춰 질문을 드려요.",
                style = HaruUpType.body1,
                color = HaruUpColor.Neutral600,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN),
            )

            Spacer(Modifier.height(SUBTITLE_TO_OPTIONS))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN),
                verticalArrangement = Arrangement.spacedBy(OPTION_SPACING),
            ) {
                uiState.personalities.forEachIndexed { index, personality ->
                    SelectButton(
                        text = personality.label,
                        isSelected = uiState.selectedIndex == index,
                        onClick = { onPersonalitySelected(index) },
                        modifier = Modifier.height(OPTION_HEIGHT),
                    )
                }
            }

            if (uiState.isLoading) {
                Spacer(Modifier.height(OPTIONS_TO_INDICATOR))
                CircularProgressIndicator(color = HaruUpColor.PrimaryBlue700)
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onNextClick,
                enabled = uiState.isNextEnabled && !uiState.isLoading,
                shape = RoundedCornerShape(CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HaruUpColor.Cta,
                    contentColor = HaruUpColor.AppWhite,
                    // iOS 는 비활성일 때도 cta 색을 유지하고 투명도만 낮춥니다.
                    disabledContainerColor = HaruUpColor.Cta,
                    disabledContentColor = HaruUpColor.AppWhite,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN)
                    .height(BUTTON_HEIGHT)
                    .alpha(if (uiState.isNextEnabled) 1f else DISABLED_BUTTON_ALPHA),
            ) {
                Text(text = "다음", style = HaruUpType.subtitle2)
            }

            Spacer(Modifier.height(BUTTON_BOTTOM_MARGIN))
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "미선택")
@Composable
private fun PersonalitySelectPreview() {
    HaruUpTheme {
        PersonalitySelectContent(
            uiState = PersonalitySelectUiState(
                personalities = PersonalitySelectViewModel.FALLBACK_PERSONALITIES,
            ),
            onPersonalitySelected = {}, onNextClick = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "선택됨")
@Composable
private fun PersonalitySelectSelectedPreview() {
    HaruUpTheme {
        PersonalitySelectContent(
            uiState = PersonalitySelectUiState(
                personalities = PersonalitySelectViewModel.FALLBACK_PERSONALITIES,
                selectedIndex = 0,
            ),
            onPersonalitySelected = {}, onNextClick = {},
        )
    }
}
