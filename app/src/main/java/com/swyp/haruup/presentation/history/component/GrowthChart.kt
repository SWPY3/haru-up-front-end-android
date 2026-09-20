package com.swyp.haruup.presentation.history.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.history.GrowthChartScale
import com.swyp.haruup.presentation.history.GrowthPoint

private val CHART_HEIGHT = 180.dp
private val BUBBLE_SPACE = 44.dp
private val X_LABEL_HEIGHT = 24.dp
private val LINE_WIDTH = 3.dp
private val POINT_OUTER = 12.dp
private val POINT_INNER = 8.dp
private val LAST_POINT_GLOW = 30.dp
private val LAST_POINT_OUTER = 14.dp
private val LAST_POINT_INNER = 10.dp

/**
 * 월별 달성일 추이 차트. iOS 의 GrowthChartSwiftUIView 에 대응합니다.
 *
 * SwiftUI Charts 에 해당하는 Compose API 가 없어 Canvas 로 직접 그립니다.
 * 마지막 달은 말풍선과 점선으로 강조합니다.
 */
@Composable
fun GrowthChart(
    points: List<GrowthPoint>,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) return

    val scale = GrowthChartScale.calculate(points)
    val density = LocalDensity.current

    Column(modifier = modifier.fillMaxWidth()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CHART_HEIGHT + BUBBLE_SPACE),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawChart(
                    points = points,
                    maxValue = scale.maxValue,
                    bubbleSpacePx = with(density) { BUBBLE_SPACE.toPx() },
                    lineWidthPx = with(density) { LINE_WIDTH.toPx() },
                )
            }

            // 점과 라벨은 Canvas 위에 컴포저블로 얹습니다.
            // 텍스트를 Canvas 에 직접 그리는 것보다 폰트 적용이 쉽습니다.
            PointOverlay(points = points, maxValue = scale.maxValue)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(X_LABEL_HEIGHT),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            points.forEach { point ->
                Text(
                    text = point.monthLabel,
                    style = HaruUpType.xText,
                    color = HaruUpColor.Neutral1000,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** 영역 채우기와 선, 마지막 점의 세로 점선을 그립니다. */
private fun DrawScope.drawChart(
    points: List<GrowthPoint>,
    maxValue: Int,
    bubbleSpacePx: Float,
    lineWidthPx: Float,
) {
    val chartTop = bubbleSpacePx
    val chartHeight = size.height - bubbleSpacePx

    // 점이 하나뿐이면 가운데에 둡니다.
    fun xOf(index: Int): Float =
        if (points.size == 1) size.width / 2
        else size.width * index / (points.size - 1)

    fun yOf(value: Int): Float =
        chartTop + chartHeight * (1f - value.toFloat() / maxValue)

    val coordinates = points.mapIndexed { index, point -> Offset(xOf(index), yOf(point.value)) }

    // 영역 채우기
    val areaPath = Path().apply {
        moveTo(coordinates.first().x, size.height)
        coordinates.forEach { lineTo(it.x, it.y) }
        lineTo(coordinates.last().x, size.height)
        close()
    }

    drawPath(
        path = areaPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                HaruUpColor.Gradient.CalendarPoint,
                HaruUpColor.Gradient.CalendarPoint.copy(alpha = 0f),
            ),
            startY = chartTop,
            endY = size.height,
        ),
    )

    // 선
    val linePath = Path().apply {
        moveTo(coordinates.first().x, coordinates.first().y)
        coordinates.drop(1).forEach { lineTo(it.x, it.y) }
    }

    drawPath(
        path = linePath,
        color = HaruUpColor.PrimaryBlue600,
        style = Stroke(width = lineWidthPx),
    )

    // 마지막 점에서 바닥까지 점선
    val last = coordinates.last()
    var y = last.y
    val dash = 4.dp.toPx()

    while (y < size.height) {
        drawLine(
            color = HaruUpColor.PrimaryBlue500,
            start = Offset(last.x, y),
            end = Offset(last.x, minOf(y + dash, size.height)),
            strokeWidth = 1.dp.toPx(),
        )
        y += dash * 2
    }
}

/** 각 점의 원과 값 라벨을 그립니다. 마지막 점만 말풍선을 씁니다. */
@Composable
private fun PointOverlay(
    points: List<GrowthPoint>,
    maxValue: Int,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        points.forEachIndexed { index, point ->
            val isLast = index == points.lastIndex

            // 값이 클수록 위에 놓입니다.
            val pointY = BUBBLE_SPACE + CHART_HEIGHT * (1f - point.value.toFloat() / maxValue)
            val circleRadius = (if (isLast) LAST_POINT_GLOW else POINT_OUTER) / 2

            Box(modifier = Modifier.weight(1f)) {
                // 아래쪽을 기준으로 잡으면 라벨 높이를 몰라도 점 위치를 정확히 맞출 수 있습니다.
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = CHART_HEIGHT + BUBBLE_SPACE - pointY - circleRadius),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (isLast) {
                        ValueBubble(value = point.value)
                    } else {
                        Text(
                            text = "${point.value}일",
                            style = HaruUpType.body5,
                            color = HaruUpColor.Neutral700,
                        )
                    }

                    PointCircle(isLast = isLast)
                }
            }
        }
    }
}

@Composable
private fun PointCircle(isLast: Boolean) {
    if (isLast) {
        Box(
            modifier = Modifier
                .size(LAST_POINT_GLOW)
                .background(HaruUpColor.Shadow.CalendarPoint, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(LAST_POINT_OUTER)
                    .background(HaruUpColor.AppWhite, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(LAST_POINT_INNER)
                        .background(HaruUpColor.PrimaryBlue600, CircleShape),
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .size(POINT_OUTER)
                .background(HaruUpColor.AppWhite, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(POINT_INNER)
                    .background(HaruUpColor.PrimaryBlue600, CircleShape),
            )
        }
    }
}

/** 마지막 달을 강조하는 파란 말풍선입니다. */
@Composable
private fun ValueBubble(value: Int) {
    Text(
        text = "${value}일",
        style = HaruUpType.level,
        color = HaruUpColor.AppWhite,
        modifier = Modifier
            .background(HaruUpColor.PrimaryBlue700, ChartBubbleShape)
            // 아래 여백에 화살표 높이를 더해 꼬리가 들어갈 자리를 만듭니다.
            .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp + 10.dp),
    )
}

private val ChartBubbleShape = object : androidx.compose.ui.graphics.Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: androidx.compose.ui.unit.Density,
    ): androidx.compose.ui.graphics.Outline {
        val arrowHeight = with(density) { 10.dp.toPx() }
        val arrowWidth = with(density) { 14.dp.toPx() }
        val radius = with(density) { 8.dp.toPx() }
        val bodyHeight = size.height - arrowHeight

        val path = Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = bodyHeight,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius),
                )
            )

            moveTo(size.width / 2 - arrowWidth / 2, bodyHeight)
            lineTo(size.width / 2, size.height)
            lineTo(size.width / 2 + arrowWidth / 2, bodyHeight)
            close()
        }

        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}
