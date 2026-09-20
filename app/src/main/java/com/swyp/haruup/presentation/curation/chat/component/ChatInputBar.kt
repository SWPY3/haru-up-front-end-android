package com.swyp.haruup.presentation.curation.chat.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType

private val CONTAINER_RADIUS = 16.dp
private val CONTENT_TOP_PADDING = 20.dp
private val CONTENT_H_PADDING = 20.dp
private val CONTENT_BOTTOM_PADDING = 8.dp
private val SEND_BUTTON_SIZE = 32.dp
private val INPUT_TO_BUTTON_SPACING = 20.dp
private val INPUT_MIN_HEIGHT = 100.dp

/**
 * 하단 입력 영역. iOS 의 inputContainerView 에 대응합니다.
 * 위쪽 모서리만 둥글고 상단에 1dp 구분선이 있습니다.
 */
@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = RoundedCornerShape(topStart = CONTAINER_RADIUS, topEnd = CONTAINER_RADIUS)
    val canSend = enabled && text.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HaruUpColor.Neutral50),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = CONTENT_H_PADDING,
                    end = CONTENT_H_PADDING,
                    top = CONTENT_TOP_PADDING,
                    bottom = CONTENT_BOTTOM_PADDING,
                ),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = INPUT_MIN_HEIGHT),
            ) {
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = HaruUpType.body1,
                        color = HaruUpColor.Neutral300,
                    )
                }

                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    enabled = enabled,
                    textStyle = HaruUpType.body1.copy(color = HaruUpColor.Neutral1000),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { if (canSend) onSendClick() }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Image(
                painter = painterResource(
                    if (canSend) R.drawable.ic_button_blue else R.drawable.ic_button_gray
                ),
                contentDescription = "보내기",
                modifier = Modifier
                    .padding(start = INPUT_TO_BUTTON_SPACING)
                    .size(SEND_BUTTON_SIZE)
                    .align(Alignment.Top)
                    .clickable(
                        enabled = canSend,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onSendClick,
                    ),
            )
        }
    }
}
