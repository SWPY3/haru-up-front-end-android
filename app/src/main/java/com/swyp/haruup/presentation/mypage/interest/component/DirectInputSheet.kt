package com.swyp.haruup.presentation.mypage.interest.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.component.BottomSheetDialog
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val H_MARGIN = 20.dp
private val TITLE_TOP = 40.dp
private val TITLE_TO_FIELD = 20.dp
private val FIELD_HEIGHT = 50.dp
private val FIELD_LEADING = 10.dp
private val FIELD_LINE_HEIGHT = 2.dp
private val FIELD_TO_COUNT = 8.dp
private val CLEAR_SIZE = 24.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_RADIUS = 16.dp
private val BUTTON_BOTTOM = 10.dp
private val COUNT_TO_BUTTON = 24.dp

private const val MIN_LENGTH = 2
private const val MAX_LENGTH = 20

/** 입력칸이 포커스를 잃었을 때 밑줄 색의 투명도입니다. (iOS 와 동일) */
private const val UNFOCUSED_LINE_ALPHA = 0.3f

/**
 * 세부 관심사나 목표를 직접 적는 바텀시트.
 * iOS 의 GoalInputBottomSheet 에 대응합니다.
 *
 * iOS 는 입력한 목표가 세부 관심사와 맞는지 서버에 물어보게 되어 있지만
 * 그 호출이 주석 처리되어 늘 통과합니다. 그래서 3회 실패 잠금과 30분 타이머도 옮기지 않았습니다.
 */
@Composable
fun DirectInputSheet(
    title: String,
    initialText: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initialText.orEmpty()) }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val trimmedLength = text.trim().length
    val isValid = trimmedLength in MIN_LENGTH..MAX_LENGTH

    BottomSheetDialog(onDismiss = onDismiss, modifier = Modifier.imePadding()) {
        Spacer(Modifier.height(TITLE_TOP))

        Text(
            text = title,
            style = HaruUpType.subtitle1,
            color = HaruUpColor.AppBlack,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(TITLE_TO_FIELD))

        Column(modifier = Modifier.padding(horizontal = H_MARGIN)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FIELD_HEIGHT)
                    .padding(horizontal = FIELD_LEADING),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (text.isEmpty()) {
                        Text(
                            text = "${MIN_LENGTH}~${MAX_LENGTH}자로 입력해 주세요.",
                            style = HaruUpType.body1,
                            color = HaruUpColor.Neutral300,
                        )
                    }

                    BasicTextField(
                        value = text,
                        // 길이 제한을 넘는 입력은 아예 받지 않습니다. (iOS 의 delegate 와 같은 동작)
                        onValueChange = { if (it.length <= MAX_LENGTH) text = it },
                        singleLine = true,
                        textStyle = HaruUpType.body1.copy(color = HaruUpColor.AppBlack),
                        cursorBrush = SolidColor(HaruUpColor.Cta),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { if (isValid) onConfirm(text.trim()) },
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged { isFocused = it.isFocused },
                    )
                }

                if (text.isNotEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.ic_xmark),
                        contentDescription = "입력 지우기",
                        modifier = Modifier
                            .size(CLEAR_SIZE)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { text = "" },
                            ),
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FIELD_LINE_HEIGHT)
                    .background(
                        if (isFocused) HaruUpColor.Cta
                        else HaruUpColor.Cta.copy(alpha = UNFOCUSED_LINE_ALPHA)
                    ),
            )

            Spacer(Modifier.height(FIELD_TO_COUNT))

            Text(
                text = "${text.length}/$MAX_LENGTH",
                style = HaruUpType.caption3,
                color = HaruUpColor.Neutral400,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
        }

        Spacer(Modifier.height(COUNT_TO_BUTTON))

        val shape = RoundedCornerShape(BUTTON_RADIUS)

        Box(
            modifier = Modifier
                .padding(horizontal = H_MARGIN)
                .fillMaxWidth()
                .height(BUTTON_HEIGHT)
                .clip(shape)
                .background(if (isValid) HaruUpColor.Cta else HaruUpColor.Neutral200, shape)
                .clickable(enabled = isValid) { onConfirm(text.trim()) },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "완료", style = HaruUpType.subtitle2, color = HaruUpColor.AppWhite)
        }

        Spacer(Modifier.height(BUTTON_BOTTOM))
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun DirectInputSheetPreview() {
    HaruUpTheme {
        DirectInputSheet(
            title = "원하는 목표를 입력해주세요.",
            initialText = "스페인어 회화",
            onConfirm = {}, onDismiss = {},
        )
    }
}
