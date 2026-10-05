package com.swyp.haruup.presentation.chart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.data.model.ChartItem
import com.swyp.haruup.presentation.chart.component.ChartEmptyCard
import com.swyp.haruup.presentation.chart.component.ChartFilterBar
import com.swyp.haruup.presentation.chart.component.ChartFilterSheet
import com.swyp.haruup.presentation.chart.component.ChartRankingCard

private val H_MARGIN = 20.dp
private val TITLE_TOP = 20.dp
private val TITLE_TO_INFO = 6.dp
private val INFO_ICON_SIZE = 24.dp
private val TITLE_TO_FILTER = 10.dp
private val FILTER_TO_CONTENT = 20.dp
private val CONTENT_BOTTOM = 46.dp

private val TOOLTIP_WIDTH = 220.dp
private val TOOLTIP_HEIGHT = 45.dp

/** 말풍선 꼬리가 i 버튼을 가리키도록 왼쪽으로 당겨 둔 값입니다. (iOS 와 같은 수치) */
private val TOOLTIP_X_OFFSET = (-51).dp
private val TOOLTIP_Y_GAP = 8.dp

/**
 * 메인 탭의 차트. iOS 의 ChartRankingViewController 에 대응합니다.
 */
@Composable
fun ChartScreen(
    modifier: Modifier = Modifier,
    viewModel: ChartViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChartContent(
        uiState = uiState,
        onInfoClick = viewModel::onInfoClick,
        onTooltipDismiss = viewModel::onTooltipDismiss,
        onFilterClick = viewModel::onFilterClick,
        onFilterSheetDismiss = viewModel::onFilterSheetDismiss,
        onFilterApplied = viewModel::onFilterApplied,
        onTagRemove = viewModel::onTagRemoved,
        onResetClick = viewModel::onResetClick,
        modifier = modifier,
    )
}

@Composable
private fun ChartContent(
    uiState: ChartUiState,
    onInfoClick: () -> Unit,
    onTooltipDismiss: () -> Unit,
    onFilterClick: () -> Unit,
    onFilterSheetDismiss: () -> Unit,
    onFilterApplied: (List<String>) -> Unit,
    onTagRemove: (String) -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10)
            .statusBarsPadding()
            .padding(horizontal = H_MARGIN)
            .padding(top = TITLE_TOP, bottom = CONTENT_BOTTOM),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "월간 미션 차트 TOP5",
                style = HaruUpType.title3,
                color = HaruUpColor.AppBlack,
            )

            Spacer(Modifier.width(TITLE_TO_INFO))

            InfoButtonWithTooltip(
                isTooltipVisible = uiState.isTooltipVisible,
                onClick = onInfoClick,
                onTooltipDismiss = onTooltipDismiss,
            )
        }

        Spacer(Modifier.height(TITLE_TO_FILTER))

        ChartFilterBar(
            selectedTags = uiState.selectedTags,
            onFilterClick = onFilterClick,
            onResetClick = onResetClick,
            onTagRemove = onTagRemove,
        )

        Spacer(Modifier.height(FILTER_TO_CONTENT))

        if (uiState.hasData) {
            ChartRankingCard(items = uiState.items)
        } else {
            ChartEmptyCard()
        }
    }

    if (uiState.isFilterSheetVisible) {
        ChartFilterSheet(
            initialTags = uiState.selectedTags,
            onApply = onFilterApplied,
            onDismiss = onFilterSheetDismiss,
        )
    }
}

/**
 * i 버튼과, 그 아래에 뜨는 말풍선입니다.
 *
 * 말풍선은 아래쪽 내용 위에 겹쳐 떠야 해서 Popup 으로 띄웁니다.
 * 같은 Column 안에 두면 뒤에 오는 형제들이 위에 그려져 가려집니다.
 */
@Composable
private fun InfoButtonWithTooltip(
    isTooltipVisible: Boolean,
    onClick: () -> Unit,
    onTooltipDismiss: () -> Unit,
) {
    Box {
        Image(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = "월간 미션 차트 설명",
            modifier = Modifier
                .size(INFO_ICON_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        )

        if (isTooltipVisible) {
            val density = LocalDensity.current
            val offset = with(density) {
                IntOffset(
                    x = TOOLTIP_X_OFFSET.roundToPx(),
                    y = (INFO_ICON_SIZE + TOOLTIP_Y_GAP).roundToPx(),
                )
            }

            Popup(
                alignment = Alignment.TopStart,
                offset = offset,
                onDismissRequest = onTooltipDismiss,
                properties = PopupProperties(focusable = true),
            ) {
                Image(
                    painter = painterResource(R.drawable.image_chart_tooltip),
                    contentDescription = "월간 가장 많이 선택한 미션이에요.",
                    modifier = Modifier.size(width = TOOLTIP_WIDTH, height = TOOLTIP_HEIGHT),
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChartPreview() {
    HaruUpTheme {
        ChartContent(
            uiState = ChartUiState(
                items = listOf(
                    ChartItem(1, "스쿼트 실시", listOf("체력관리 및 운동", "헬스"), 4),
                    ChartItem(2, "모의 시험 풀기", listOf("자격증 공부", "기술 분야"), 3),
                    ChartItem(3, "칼로리 기록하기", listOf("체력관리 및 운동", "자전거"), 3),
                    ChartItem(4, "자전거 주행하기", listOf("체력관리 및 운동", "자전거"), 3),
                    ChartItem(5, "영어 단어 외우기", listOf("외국어 공부", "영어"), 2),
                ),
            ),
            onInfoClick = {}, onTooltipDismiss = {}, onFilterClick = {},
            onFilterSheetDismiss = {}, onFilterApplied = {}, onTagRemove = {}, onResetClick = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChartEmptyPreview() {
    HaruUpTheme {
        ChartContent(
            uiState = ChartUiState(selectedTags = listOf("여성", "25 - 29세", "개발자")),
            onInfoClick = {}, onTooltipDismiss = {}, onFilterClick = {},
            onFilterSheetDismiss = {}, onFilterApplied = {}, onTagRemove = {}, onResetClick = {},
        )
    }
}
