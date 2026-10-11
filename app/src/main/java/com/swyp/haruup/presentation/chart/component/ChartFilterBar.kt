package com.swyp.haruup.presentation.chart.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val BAR_HEIGHT = 44.dp
private val FILTER_ICON_SIZE = 32.dp
private val RESET_ICON_SIZE = 32.dp
private val FILTER_TO_NEXT = 8.dp
private val RESET_TO_CHIPS = 4.dp
private val CHIP_SPACING = 8.dp
private val CHIP_HEIGHT = 32.dp
private val CHIP_RADIUS = 12.dp
private val CHIP_LEADING = 10.dp
private val CHIP_TRAILING = 5.dp
private val CHIP_LABEL_TO_CLOSE = 2.dp
private val CLOSE_TOUCH_SIZE = 24.dp
private val CLOSE_ICON_SIZE = 10.dp

/**
 * 제목 아래의 검색조건 줄.
 * 조건이 없으면 안내 문구를, 있으면 초기화 버튼과 조건 칩을 보여줍니다.
 * iOS 의 filterButton / resetButton / filterLabel / filterScrollView 에 대응합니다.
 */
@Composable
fun ChartFilterBar(
    selectedTags: List<String>,
    onFilterClick: () -> Unit,
    onResetClick: () -> Unit,
    onTagRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isActive = selectedTags.isNotEmpty()

    Row(
        modifier = modifier.height(BAR_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(
                if (isActive) R.drawable.ic_filter_selected else R.drawable.ic_filter
            ),
            contentDescription = "검색조건",
            modifier = Modifier
                .size(FILTER_ICON_SIZE)
                .clickable(onClick = onFilterClick),
        )

        Spacer(Modifier.width(FILTER_TO_NEXT))

        if (isActive) {
            Image(
                painter = painterResource(R.drawable.ic_reset),
                contentDescription = "검색조건 초기화",
                modifier = Modifier
                    .size(RESET_ICON_SIZE)
                    .clickable(onClick = onResetClick),
            )

            Spacer(Modifier.width(RESET_TO_CHIPS))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(CHIP_SPACING),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                selectedTags.forEach { tag ->
                    SelectedTagChip(tag = tag, onRemove = { onTagRemove(tag) })
                }
            }
        } else {
            Text(
                text = "검색조건을 추가해보세요.",
                style = HaruUpType.body4,
                color = HaruUpColor.Neutral500,
            )
        }
    }
}

@Composable
private fun SelectedTagChip(tag: String, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(CHIP_RADIUS)

    Row(
        modifier = Modifier
            .height(CHIP_HEIGHT)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .border(1.dp, HaruUpColor.Neutral50, shape)
            .padding(start = CHIP_LEADING, end = CHIP_TRAILING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = tag, style = HaruUpType.body4, color = HaruUpColor.Neutral800)

        Spacer(Modifier.width(CHIP_LABEL_TO_CLOSE))

        // 아이콘 자체는 10dp 로 작아서 누를 수 있는 영역을 24dp 로 넓혀 둡니다.
        Box(
            modifier = Modifier
                .size(CLOSE_TOUCH_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onRemove,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_cancel),
                contentDescription = "$tag 조건 제거",
                modifier = Modifier.size(CLOSE_ICON_SIZE),
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChartFilterBarPreview() {
    HaruUpTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChartFilterBar(
                selectedTags = emptyList(),
                onFilterClick = {}, onResetClick = {}, onTagRemove = {},
            )
            ChartFilterBar(
                selectedTags = listOf("여성", "25 - 29세", "개발자"),
                onFilterClick = {}, onResetClick = {}, onTagRemove = {},
            )
        }
    }
}
