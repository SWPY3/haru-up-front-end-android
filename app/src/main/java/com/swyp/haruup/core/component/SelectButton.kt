package com.swyp.haruup.core.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType

private val CORNER_RADIUS = 16.dp
private val VERTICAL_PADDING = 16.dp
private val HORIZONTAL_PADDING = 20.dp
private val SELECTED_BORDER_WIDTH = 2.dp

/**
 * iOS 의 Component/Buttons/SelectButton 에 대응합니다.
 * 목록에서 하나를 고르는 화면들이 공통으로 씁니다.
 */
@Composable
fun SelectButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CORNER_RADIUS)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                color = if (isSelected) HaruUpColor.PrimaryBlue50 else HaruUpColor.Neutral10,
                shape = shape,
            )
            .then(
                if (isSelected) {
                    Modifier.border(
                        border = BorderStroke(SELECTED_BORDER_WIDTH, HaruUpColor.Cta),
                        shape = shape,
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = HORIZONTAL_PADDING, vertical = VERTICAL_PADDING),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = HaruUpType.body1,
            color = if (isSelected) HaruUpColor.Cta else HaruUpColor.Neutral1000,
        )
    }
}
