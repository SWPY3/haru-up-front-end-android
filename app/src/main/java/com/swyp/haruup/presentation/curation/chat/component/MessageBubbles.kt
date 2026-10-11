package com.swyp.haruup.presentation.curation.chat.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.curation.chat.ChatMessage

private val BUBBLE_RADIUS = 16.dp
private val BUBBLE_H_PADDING = 14.dp
private val BUBBLE_V_PADDING = 12.dp
private val ROW_H_MARGIN = 16.dp
private val ROW_V_MARGIN = 8.dp

/** 봇 말풍선은 가로 80% 까지, 사용자 말풍선은 70% 까지 차지합니다. */
private const val BOT_MAX_WIDTH_RATIO = 0.8f
private const val USER_MAX_WIDTH_RATIO = 0.7f

/**
 * 봇 메시지. iOS 의 BotMessageCell 에 대응합니다.
 * 왼쪽 위 모서리만 각지게 두어 꼬리 방향을 표현합니다.
 */
@Composable
fun BotMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = ROW_H_MARGIN, top = ROW_V_MARGIN),
        horizontalAlignment = Alignment.Start,
    ) {
        MaxWidthRow(ratio = BOT_MAX_WIDTH_RATIO) {
            val shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = BUBBLE_RADIUS,
                bottomEnd = BUBBLE_RADIUS,
                bottomStart = BUBBLE_RADIUS,
            )

            Text(
                text = message.annotated(),
                style = HaruUpType.body4,
                modifier = Modifier
                    .clip(shape)
                    .background(HaruUpColor.AppWhite, shape)
                    .border(1.dp, HaruUpColor.Neutral50, shape)
                    .padding(horizontal = BUBBLE_H_PADDING, vertical = BUBBLE_V_PADDING),
            )
        }

        if (message.subtitleText != null) {
            Text(
                text = message.subtitleText,
                style = HaruUpType.caption3,
                color = HaruUpColor.Neutral400,
                maxLines = 1,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp),
            )
        }
    }
}

/**
 * 사용자 메시지. iOS 의 UserMessageCell 에 대응합니다.
 * 오른쪽 위 모서리만 각집니다.
 */
@Composable
fun UserMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(end = ROW_H_MARGIN, top = ROW_V_MARGIN, bottom = ROW_V_MARGIN),
        horizontalArrangement = Arrangement.End,
    ) {
        MaxWidthRow(ratio = USER_MAX_WIDTH_RATIO) {
            val shape = RoundedCornerShape(
                topStart = BUBBLE_RADIUS,
                topEnd = 0.dp,
                bottomEnd = BUBBLE_RADIUS,
                bottomStart = BUBBLE_RADIUS,
            )

            Text(
                text = message.text,
                style = HaruUpType.body4,
                color = HaruUpColor.AppWhite,
                modifier = Modifier
                    .clip(shape)
                    .background(HaruUpColor.Neutral900, shape)
                    .padding(horizontal = BUBBLE_H_PADDING, vertical = BUBBLE_V_PADDING),
            )
        }
    }
}

/**
 * 자식이 부모 너비의 [ratio] 를 넘지 않도록 제한합니다.
 * iOS 의 widthAnchor lessThanOrEqualTo multiplier 에 대응합니다.
 */
@Composable
private fun MaxWidthRow(ratio: Float, content: @Composable () -> Unit) {
    Layout(content = content) { measurables, constraints ->
        val maxWidth = (constraints.maxWidth * ratio).toInt()
        val placeable = measurables.first().measure(
            constraints.copy(minWidth = 0, maxWidth = maxWidth)
        )
        layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }
}

/** 강조 문구가 있으면 그 부분만 다른 색·굵기로 칠합니다. */
private fun ChatMessage.annotated(): AnnotatedString {
    val defaultColor = if (isError) HaruUpColor.SecondaryRed200 else HaruUpColor.Neutral800
    val highlight = highlightedText
    val start = highlight?.let { text.indexOf(it) } ?: -1

    if (highlight == null || start < 0) {
        return buildAnnotatedString {
            withStyle(SpanStyle(color = defaultColor)) { append(text) }
        }
    }

    return buildAnnotatedString {
        withStyle(SpanStyle(color = HaruUpColor.Neutral800)) {
            append(text.substring(0, start))
        }
        withStyle(
            SpanStyle(
                color = if (isError) HaruUpColor.SecondaryRed200 else HaruUpColor.AppBlack,
                fontWeight = FontWeight.Bold,
            )
        ) {
            append(highlight)
        }
        withStyle(SpanStyle(color = HaruUpColor.Neutral800)) {
            append(text.substring(start + highlight.length))
        }
    }
}
