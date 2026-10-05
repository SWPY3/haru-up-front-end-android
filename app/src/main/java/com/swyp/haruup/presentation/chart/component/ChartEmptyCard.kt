package com.swyp.haruup.presentation.chart.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val CARD_RADIUS = 20.dp
private val CARD_HEIGHT = 280.dp
private val CARD_ELEVATION = 10.dp
private val GRAPH_TOP = 40.dp
private val GRAPH_WIDTH = 100.dp
private val GRAPH_HEIGHT = 80.dp
private val GRAPH_TO_TITLE = 24.dp
private val TITLE_TO_DESCRIPTION = 12.dp

/**
 * 차트에 보여줄 데이터가 아직 없을 때의 카드.
 * iOS 의 emptyCardView 에 대응합니다.
 */
@Composable
fun ChartEmptyCard(modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(CARD_RADIUS)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT)
            .shadow(elevation = CARD_ELEVATION, shape = shape)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(GRAPH_TOP))

        Image(
            painter = painterResource(R.drawable.image_chart_empty),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = GRAPH_WIDTH, height = GRAPH_HEIGHT),
        )

        Spacer(Modifier.height(GRAPH_TO_TITLE))

        Text(
            text = "아직 충분한 데이터가 모이지 않았어요!",
            style = HaruUpType.subtitle2,
            color = HaruUpColor.Neutral1000,
        )

        Spacer(Modifier.height(TITLE_TO_DESCRIPTION))

        Text(
            text = "많이 선택된 미션 TOP 5를 확인할 수 있는\n월간 미션 차트가 곧 업데이트돼요.",
            style = HaruUpType.body4,
            color = HaruUpColor.Neutral900,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChartEmptyCardPreview() {
    HaruUpTheme {
        ChartEmptyCard(modifier = Modifier.padding(20.dp))
    }
}
