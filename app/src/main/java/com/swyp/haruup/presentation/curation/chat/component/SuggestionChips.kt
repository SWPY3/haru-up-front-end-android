package com.swyp.haruup.presentation.curation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType

private val CHIP_RADIUS = 20.dp
private val CHIP_H_PADDING = 14.dp
private val CHIP_V_PADDING = 8.dp
private val CHIP_SPACING = 8.dp
private val ROW_H_MARGIN = 16.dp

/**
 * 예시 답변 칩. iOS 의 SuggestionChipsCell / ChipCollectionCell 에 대응합니다.
 * 가로로 스크롤되며, 탭하면 그 문구를 그대로 답변으로 보냅니다.
 */
@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onChipClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp),
        contentPadding = PaddingValues(horizontal = ROW_H_MARGIN),
        horizontalArrangement = Arrangement.spacedBy(CHIP_SPACING),
    ) {
        items(items = suggestions, key = { it }) { suggestion ->
            val shape = RoundedCornerShape(CHIP_RADIUS)

            Text(
                text = suggestion,
                style = HaruUpType.body4,
                color = HaruUpColor.Neutral1000,
                modifier = Modifier
                    .clip(shape)
                    .background(HaruUpColor.Neutral100, shape)
                    .clickable { onChipClick(suggestion) }
                    .padding(horizontal = CHIP_H_PADDING, vertical = CHIP_V_PADDING),
            )
        }
    }
}
