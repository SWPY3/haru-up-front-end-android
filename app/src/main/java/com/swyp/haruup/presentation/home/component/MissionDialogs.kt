package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val ALERT_RADIUS = 24.dp
private val ALERT_H_MARGIN = 28.dp
private val ALERT_PADDING = 24.dp
private val CONFIRM_HEIGHT = 45.dp
private val DELETE_HEIGHT = 56.dp
private val CANCEL_HEIGHT = 48.dp
private val CORNER_RADIUS = 16.dp

/**
 * 미션을 완료했을 때 가운데에 뜨는 축하 알림.
 * iOS 의 MissionCompleteView 에 대응합니다.
 */
@Composable
fun MissionCompleteDialog(
    expEarned: Int,
    onConfirm: () -> Unit,
) {
    Dialog(
        onDismissRequest = onConfirm,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HaruUpColor.Background.BottomSheet),
            contentAlignment = Alignment.Center,
        ) {
            val shape = RoundedCornerShape(ALERT_RADIUS)

            Column(
                modifier = Modifier
                    .padding(horizontal = ALERT_H_MARGIN)
                    .clip(shape)
                    .background(HaruUpColor.AppWhite, shape)
                    .padding(ALERT_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "🎉 미션 완료 🎉",
                    style = HaruUpType.subtitle1,
                    color = HaruUpColor.AppBlack,
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = HaruUpColor.Cta)) { append("$expEarned 경험치") }
                        withStyle(SpanStyle(color = HaruUpColor.AppBlack)) {
                            append("를 획득했어요!\n오늘도 한걸음 성장했어요 ✨")
                        }
                    },
                    style = HaruUpType.body1,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onConfirm,
                    shape = RoundedCornerShape(CORNER_RADIUS),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HaruUpColor.Cta,
                        contentColor = HaruUpColor.AppWhite,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(CONFIRM_HEIGHT),
                ) {
                    Text(text = "확인", style = HaruUpType.subtitle2)
                }
            }
        }
    }
}

/**
 * 미션 삭제 전 한 번 더 확인하는 시트.
 * iOS 의 MissionDeleteView 에 대응합니다.
 */
@Composable
fun MissionDeleteConfirmSheet(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
) {
    BottomSheetDialog(onDismiss = onCancel) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = "삭제 후에는 미션 복구가 불가능해요.\n그래도 미션을 삭제하시겠어요?",
            style = HaruUpType.subtitle1,
            color = HaruUpColor.AppBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = onDelete,
                shape = RoundedCornerShape(CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HaruUpColor.Cta,
                    contentColor = HaruUpColor.AppWhite,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DELETE_HEIGHT),
            ) {
                Text(text = "삭제할래요", style = HaruUpType.subtitle2)
            }

            Button(
                onClick = onCancel,
                shape = RoundedCornerShape(CORNER_RADIUS),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HaruUpColor.AppWhite,
                    contentColor = HaruUpColor.Neutral500,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CANCEL_HEIGHT),
            ) {
                Text(text = "취소", style = HaruUpType.subtitle2)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "미션 완료")
@Composable
private fun MissionCompleteDialogPreview() {
    HaruUpTheme { MissionCompleteDialog(expEarned = 250, onConfirm = {}) }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "삭제 확인")
@Composable
private fun MissionDeleteConfirmPreview() {
    HaruUpTheme { MissionDeleteConfirmSheet(onDelete = {}, onCancel = {}) }
}
