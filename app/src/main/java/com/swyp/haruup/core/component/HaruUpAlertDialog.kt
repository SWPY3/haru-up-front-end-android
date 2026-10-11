package com.swyp.haruup.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val DIALOG_WIDTH = 300.dp
private val DIALOG_RADIUS = 24.dp
private val H_PADDING = 20.dp
private val TITLE_TOP = 32.dp
private val TITLE_TO_MESSAGE = 12.dp
private val MESSAGE_TO_BUTTONS = 32.dp
private val MESSAGE_TO_SINGLE_BUTTON = 24.dp
private val BUTTON_ROW_HEIGHT = 56.dp
private val SINGLE_BUTTON_HEIGHT = 48.dp
private val SINGLE_BUTTON_RADIUS = 12.dp
private val SINGLE_BUTTON_BOTTOM = 20.dp

/**
 * 예/아니오를 묻는 알림창. iOS 의 MyPageAlertViewController(type: .confirmation) 에 대응합니다.
 *
 * 버튼 두 개가 창 아래쪽을 가로로 꽉 채우고 가운데에 세로 구분선이 들어갑니다.
 */
@Composable
fun HaruUpConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertFrame(onDismiss = onCancel) {
        AlertText(title = title, message = message)

        Spacer(Modifier.height(MESSAGE_TO_BUTTONS))

        Divider()

        Row(modifier = Modifier.height(BUTTON_ROW_HEIGHT)) {
            TextButtonCell(
                text = cancelText,
                color = HaruUpColor.Neutral700,
                onClick = onCancel,
                modifier = Modifier.weight(1f),
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(HaruUpColor.Neutral50),
            )

            TextButtonCell(
                text = confirmText,
                color = HaruUpColor.PrimaryBlue700,
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/**
 * 확인 버튼 하나만 있는 알림창. iOS 의 MyPageAlertViewController(type: .success) 에 대응합니다.
 */
@Composable
fun HaruUpNoticeDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
) {
    AlertFrame(onDismiss = onConfirm) {
        AlertText(title = title, message = message)

        Spacer(Modifier.height(MESSAGE_TO_SINGLE_BUTTON))

        val shape = RoundedCornerShape(SINGLE_BUTTON_RADIUS)

        Box(
            modifier = Modifier
                .padding(horizontal = H_PADDING)
                .fillMaxWidth()
                .height(SINGLE_BUTTON_HEIGHT)
                .clip(shape)
                .background(HaruUpColor.PrimaryBlue700, shape)
                .clickable(onClick = onConfirm),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = confirmText, style = HaruUpType.body3, color = HaruUpColor.AppWhite)
        }

        Spacer(Modifier.height(SINGLE_BUTTON_BOTTOM))
    }
}

@Composable
private fun AlertFrame(
    onDismiss: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HaruUpColor.Background.BottomSheet),
            contentAlignment = Alignment.Center,
        ) {
            val shape = RoundedCornerShape(DIALOG_RADIUS)

            Column(
                modifier = Modifier
                    .width(DIALOG_WIDTH)
                    .clip(shape)
                    .background(HaruUpColor.AppWhite, shape),
                content = content,
            )
        }
    }
}

@Composable
private fun AlertText(title: String, message: String) {
    Spacer(Modifier.height(TITLE_TOP))

    Text(
        text = title,
        style = HaruUpType.subtitle1,
        color = HaruUpColor.AppBlack,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PADDING),
    )

    Spacer(Modifier.height(TITLE_TO_MESSAGE))

    Text(
        text = message,
        style = HaruUpType.body1,
        color = HaruUpColor.Neutral900,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PADDING),
    )
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(HaruUpColor.Neutral50),
    )
}

@Composable
private fun TextButtonCell(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, style = HaruUpType.body1, color = color)
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun HaruUpConfirmDialogPreview() {
    HaruUpTheme {
        HaruUpConfirmDialog(
            title = "로그아웃을 진행할까요?",
            message = "다음 접속 시 계정 정보를\n다시 입력해야 해요.",
            confirmText = "예",
            cancelText = "아니오",
            onConfirm = {}, onCancel = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun HaruUpNoticeDialogPreview() {
    HaruUpTheme {
        HaruUpNoticeDialog(
            title = "탈퇴가 완료되었습니다.",
            message = "더 좋은 서비스를 준비할게요.\n다음에 다시 만나요!",
            confirmText = "확인",
            onConfirm = {},
        )
    }
}
