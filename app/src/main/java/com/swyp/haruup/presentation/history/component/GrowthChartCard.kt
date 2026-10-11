package com.swyp.haruup.presentation.history.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.history.GrowthPoint
import com.swyp.haruup.presentation.history.growthDescription

private val CARD_RADIUS = 24.dp
private val CARD_PADDING = 24.dp
private val TITLE_TO_DESCRIPTION = 4.dp
private val DESCRIPTION_TO_CHART = 16.dp

/**
 * 성장 차트 카드. iOS 의 chartCardView 영역에 대응합니다.
 */
@Composable
fun GrowthChartCard(
    points: List<GrowthPoint>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)
    val description = growthDescription(points)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .padding(CARD_PADDING),
    ) {
        Text(text = "성장차트", style = HaruUpType.subtitle1, color = HaruUpColor.AppBlack)

        Spacer(Modifier.height(TITLE_TO_DESCRIPTION))

        Text(
            text = buildAnnotatedString {
                // 변화한 일수만 강조 색으로 칠합니다.
                val start = description.text.indexOf(description.highlight)

                if (start < 0) {
                    withStyle(SpanStyle(color = HaruUpColor.Neutral900)) { append(description.text) }
                } else {
                    withStyle(SpanStyle(color = HaruUpColor.Neutral900)) {
                        append(description.text.substring(0, start))
                    }
                    withStyle(SpanStyle(color = HaruUpColor.Cta)) { append(description.highlight) }
                    withStyle(SpanStyle(color = HaruUpColor.Neutral900)) {
                        append(description.text.substring(start + description.highlight.length))
                    }
                }
            },
            style = HaruUpType.description,
        )

        Spacer(Modifier.height(DESCRIPTION_TO_CHART))

        GrowthChart(points = points)
    }
}
