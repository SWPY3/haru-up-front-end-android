package com.swyp.haruup.presentation.chart.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.component.BottomSheetDialog
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.chart.ChartFilter

/** iOS 가 화면 높이의 70% 로 고정한 시트입니다. */
private const val SHEET_HEIGHT_RATIO = 0.7f

private val H_MARGIN = 20.dp
private val TITLE_TOP = 34.5.dp
private val TITLE_TO_DIVIDER = 12.dp
private val DIVIDER_TO_CONTENT = 28.dp
private val SECTION_SPACING = 26.dp
private val HEADER_TO_TAGS = 12.dp
private val TAG_SPACING = 8.dp
private val TAG_RADIUS = 10.dp
private val TAG_H_PADDING = 16.dp
private val TAG_V_PADDING = 8.dp
private val CONTENT_BOTTOM = 20.dp
private val FOOTER_SPACING = 8.dp
private val FOOTER_HEIGHT = 52.dp
private val FOOTER_BOTTOM = 10.dp
private val BUTTON_RADIUS = 12.dp

/** iOS 가 닫기 버튼 너비를 "결과 보기" 버튼의 0.35 배로 잡아 둔 비율입니다. */
private const val CLOSE_BUTTON_WEIGHT = 0.35f

/**
 * 검색조건을 고르는 바텀시트. iOS 의 FilterModalViewController 에 대응합니다.
 *
 * 고르는 동안의 선택 상태는 시트 안에만 둡니다.
 * "결과 보기" 를 눌러야 [onApply] 로 넘어가고, 닫기로 빠져나가면 아무것도 바뀌지 않습니다. (iOS 와 동일)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartFilterSheet(
    initialTags: List<String>,
    onApply: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    val selected = remember { mutableStateListOf<String>().apply { addAll(initialTags) } }

    BottomSheetDialog(
        onDismiss = onDismiss,
        modifier = Modifier.fillMaxHeight(SHEET_HEIGHT_RATIO),
    ) {
        Spacer(Modifier.height(TITLE_TOP))

        Text(
            text = "필터",
            style = HaruUpType.subtitle1,
            color = HaruUpColor.AppBlack,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(TITLE_TO_DIVIDER))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HaruUpColor.Neutral50),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = H_MARGIN)
                .padding(top = DIVIDER_TO_CONTENT, bottom = CONTENT_BOTTOM),
            verticalArrangement = Arrangement.spacedBy(SECTION_SPACING),
        ) {
            ChartFilter.SECTIONS.forEach { section ->
                Column {
                    Text(
                        text = section.title,
                        style = HaruUpType.subtitle2,
                        color = HaruUpColor.Neutral1000,
                    )

                    Spacer(Modifier.height(HEADER_TO_TAGS))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(TAG_SPACING),
                        verticalArrangement = Arrangement.spacedBy(TAG_SPACING),
                    ) {
                        section.tags.forEach { tag ->
                            FilterTagButton(
                                text = tag,
                                isSelected = tag in selected,
                                onClick = {
                                    if (tag in selected) selected.remove(tag) else selected.add(tag)
                                },
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = H_MARGIN)
                .padding(bottom = FOOTER_BOTTOM)
                .height(FOOTER_HEIGHT),
            horizontalArrangement = Arrangement.spacedBy(FOOTER_SPACING),
        ) {
            FooterButton(
                text = "닫기",
                background = HaruUpColor.AppWhite,
                textColor = HaruUpColor.AppBlack,
                borderColor = HaruUpColor.Neutral100,
                onClick = onDismiss,
                modifier = Modifier.weight(CLOSE_BUTTON_WEIGHT),
            )
            FooterButton(
                text = "결과 보기",
                background = HaruUpColor.Cta,
                textColor = HaruUpColor.AppWhite,
                borderColor = null,
                // 고른 순서가 아니라 섹션에 적힌 순서대로 칩이 보이도록 정렬합니다.
                onClick = { onApply(ChartFilter.sortBySectionOrder(selected)) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun FilterTagButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(TAG_RADIUS)

    val background = if (isSelected) HaruUpColor.PrimaryBlue50 else HaruUpColor.AppWhite
    val borderColor = if (isSelected) HaruUpColor.PrimaryBlue700 else HaruUpColor.Neutral100
    val textColor = if (isSelected) HaruUpColor.PrimaryBlue700 else HaruUpColor.Neutral800

    Text(
        text = text,
        style = HaruUpType.body4,
        color = textColor,
        modifier = Modifier
            .clip(shape)
            .background(background, shape)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = TAG_H_PADDING, vertical = TAG_V_PADDING),
    )
}

@Composable
private fun FooterButton(
    text: String,
    background: Color,
    textColor: Color,
    borderColor: Color?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(BUTTON_RADIUS)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .background(background, shape)
            .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = HaruUpType.subtitle2, color = textColor)
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChartFilterSheetPreview() {
    HaruUpTheme {
        ChartFilterSheet(
            initialTags = listOf("여성", "개발자"),
            onApply = {}, onDismiss = {},
        )
    }
}
