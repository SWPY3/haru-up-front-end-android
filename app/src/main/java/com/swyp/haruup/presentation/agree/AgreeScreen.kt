package com.swyp.haruup.presentation.agree

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.agree.component.AgreementRow

private val HORIZONTAL_MARGIN = 20.dp
private val BACK_BUTTON_SIZE = 20.dp
private val BACK_TOP_MARGIN = 10.dp
private val BACK_TO_TITLE = 22.dp
private val TITLE_TO_TERMS = 40.dp
private val AROUND_SEPARATOR = 10.dp
private val BETWEEN_TERMS = 4.dp
private val CONFIRM_HEIGHT = 56.dp
private val CONFIRM_BOTTOM_MARGIN = 10.dp
private val CORNER_RADIUS = 16.dp

/**
 * iOS 의 AgreeViewController 에 대응합니다.
 * 세 항목 모두 필수라 전부 체크해야 동의하기 버튼이 활성화됩니다.
 */
@Composable
fun AgreeScreen(
    onBackClick: () -> Unit,
    onAgreed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AgreeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    AgreeContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onAllClick = viewModel::onAllClick,
        onTermClick = viewModel::onTermClick,
        onTermDetailClick = { url ->
            try {
                CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
            } catch (e: ActivityNotFoundException) {
                // 브라우저가 없는 기기에서는 아무 동작도 하지 않는다.
                // TODO: 공통 에러 처리 방식이 정해지면 토스트 등으로 알린다.
            }
        },
        onAgreed = onAgreed,
        modifier = modifier,
    )
}

@Composable
private fun AgreeContent(
    uiState: AgreeUiState,
    onBackClick: () -> Unit,
    onAllClick: () -> Unit,
    onTermClick: (TermType) -> Unit,
    onTermDetailClick: (String) -> Unit,
    onAgreed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.AppWhite)
            .systemBarsPadding(),
    ) {
        Spacer(Modifier.height(BACK_TOP_MARGIN))

        Image(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "뒤로 가기",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(horizontal = HORIZONTAL_MARGIN)
                .size(BACK_BUTTON_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick,
                ),
        )

        Spacer(Modifier.height(BACK_TO_TITLE))

        Text(
            text = "하루업 이용을 위해\n이용약관 동의가 필요해요",
            style = HaruUpType.title2,
            color = HaruUpColor.AppBlack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HORIZONTAL_MARGIN),
        )

        Spacer(Modifier.height(TITLE_TO_TERMS))

        Column(modifier = Modifier.padding(horizontal = HORIZONTAL_MARGIN)) {
            AgreementRow(
                text = "모두 동의합니다",
                isChecked = uiState.isAllChecked,
                onCheckClick = onAllClick,
                textStyle = HaruUpType.subtitle2,
            )

            Spacer(Modifier.height(AROUND_SEPARATOR))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(HaruUpColor.Neutral50),
            )

            Spacer(Modifier.height(AROUND_SEPARATOR))

            TermType.entries.forEachIndexed { index, term ->
                if (index > 0) Spacer(Modifier.height(BETWEEN_TERMS))

                AgreementRow(
                    text = term.label,
                    isChecked = uiState.isChecked(term),
                    onCheckClick = { onTermClick(term) },
                    textStyle = HaruUpType.body4,
                    onArrowClick = term.url?.let { url -> { onTermDetailClick(url) } },
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onAgreed,
            enabled = uiState.isConfirmEnabled,
            shape = RoundedCornerShape(CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = HaruUpColor.Cta,
                contentColor = HaruUpColor.AppWhite,
                // iOS 는 비활성일 때 neutral200 배경에 흰 글씨를 유지합니다.
                disabledContainerColor = HaruUpColor.Neutral200,
                disabledContentColor = HaruUpColor.AppWhite,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HORIZONTAL_MARGIN)
                .height(CONFIRM_HEIGHT),
        ) {
            Text(text = "동의하기", style = HaruUpType.subtitle2)
        }

        Spacer(Modifier.height(CONFIRM_BOTTOM_MARGIN))
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "미동의")
@Composable
private fun AgreeScreenPreview() {
    HaruUpTheme {
        AgreeContent(
            uiState = AgreeUiState(),
            onBackClick = {}, onAllClick = {}, onTermClick = {},
            onTermDetailClick = {}, onAgreed = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "전체 동의")
@Composable
private fun AgreeScreenAllCheckedPreview() {
    HaruUpTheme {
        AgreeContent(
            uiState = AgreeUiState(checked = TermType.entries.toSet()),
            onBackClick = {}, onAllClick = {}, onTermClick = {},
            onTermDetailClick = {}, onAgreed = {},
        )
    }
}
