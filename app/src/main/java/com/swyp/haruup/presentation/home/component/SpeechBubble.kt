package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType

private val CORNER_RADIUS = 22.dp
private val TAIL_WIDTH = 12.dp
private val TAIL_HEIGHT = 8.dp
private val H_PADDING = 20.dp
private val V_PADDING = 14.dp

/**
 * 캐릭터 말풍선. iOS 의 SpeechBubbleView 에 대응합니다.
 * 아래쪽 가운데에 꼬리가 달립니다.
 */
@Composable
fun SpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(color = HaruUpColor.AppWhite, shape = SpeechBubbleShape)
            .padding(
                start = H_PADDING,
                end = H_PADDING,
                top = V_PADDING,
                bottom = V_PADDING + TAIL_HEIGHT,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HaruUpType.body3,
            color = HaruUpColor.AppBlack,
            maxLines = 1,
            textAlign = TextAlign.Center,
        )
    }
}

/** 둥근 사각형 아래에 삼각 꼬리를 붙인 모양입니다. */
private val SpeechBubbleShape = object : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val radius = with(density) { CORNER_RADIUS.toPx() }
        val tailWidth = with(density) { TAIL_WIDTH.toPx() }
        val tailHeight = with(density) { TAIL_HEIGHT.toPx() }
        val bodyHeight = size.height - tailHeight

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

            // 꼬리
            moveTo(size.width / 2 - tailWidth / 2, bodyHeight)
            lineTo(size.width / 2, bodyHeight + tailHeight)
            lineTo(size.width / 2 + tailWidth / 2, bodyHeight)
            close()
        }

        return Outline.Generic(path)
    }
}
