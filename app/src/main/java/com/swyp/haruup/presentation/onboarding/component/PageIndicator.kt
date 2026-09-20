package com.swyp.haruup.presentation.onboarding.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor

private val INDICATOR_HEIGHT = 10.dp
private val DOT_SIZE = 8.dp
private val DOT_SPACING = 8.dp

/**
 * iOS 의 UIPageControl 에 대응합니다. 터치는 받지 않고 표시만 합니다.
 */
@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(INDICATOR_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(DOT_SPACING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val color by animateColorAsState(
                targetValue = if (index == currentPage) HaruUpColor.Neutral1000 else HaruUpColor.Neutral100,
                label = "indicator_color",
            )
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    .background(color = color, shape = CircleShape),
            )
        }
    }
}
