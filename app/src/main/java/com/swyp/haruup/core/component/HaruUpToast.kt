package com.swyp.haruup.core.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import kotlinx.coroutines.delay

private val TOAST_HEIGHT = 54.dp
private val TOAST_RADIUS = 27.dp
private val ICON_LEADING = 16.dp
private val ICON_SIZE = 24.dp
private val ICON_TO_TEXT = 20.dp
private val TEXT_TRAILING = 20.dp

/** iOS 의 토스트 배경색입니다. 디자인 시스템에 없는 값이라 여기서만 씁니다. */
private val TOAST_BACKGROUND = Color(0xFF90959E)

private const val FADE_MILLIS = 300
private const val HOLD_MILLIS = 2000L

/**
 * 체크 아이콘과 문구가 든 알약 모양 안내.
 * iOS 의 ProfileEditViewController.showToast 에 대응합니다.
 *
 * [message] 가 null 이 아니면 떠올랐다가 잠시 뒤 사라지고, 사라질 때 [onDismiss] 를 부릅니다.
 *
 * 체크 아이콘은 무언가를 마쳤다는 뜻이라 안내나 오류에는 [showCheckIcon] 을 꺼서 씁니다.
 */
@Composable
fun HaruUpToast(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    showCheckIcon: Boolean = true,
) {
    // message 가 null 이 되어도 사라지는 동안에는 마지막 문구를 그대로 보여 줘야 해서 붙잡아 둡니다.
    var displayed by remember { mutableStateOf("") }
    if (message != null && message != displayed) displayed = message

    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect

        delay(HOLD_MILLIS + FADE_MILLIS)
        onDismiss()
    }

    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(tween(FADE_MILLIS)),
        exit = fadeOut(tween(FADE_MILLIS)),
        modifier = modifier,
    ) {
        val shape = RoundedCornerShape(TOAST_RADIUS)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TOAST_HEIGHT)
                .clip(shape)
                .background(TOAST_BACKGROUND, shape)
                .padding(start = ICON_LEADING, end = TEXT_TRAILING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showCheckIcon) {
                Image(
                    painter = painterResource(R.drawable.ic_small_check),
                    contentDescription = null,
                    modifier = Modifier.size(ICON_SIZE),
                )

                Spacer(Modifier.width(ICON_TO_TEXT))
            }

            Text(
                text = displayed,
                style = HaruUpType.subtitle2,
                color = HaruUpColor.AppWhite,
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun HaruUpToastPreview() {
    HaruUpTheme {
        HaruUpToast(
            message = "닉네임 변경이 완료되었어요",
            onDismiss = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
