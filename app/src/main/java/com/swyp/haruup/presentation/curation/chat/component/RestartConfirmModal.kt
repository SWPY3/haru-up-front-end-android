package com.swyp.haruup.presentation.curation.chat.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val SHEET_HEIGHT = 430.dp
private val SHEET_RADIUS = 32.dp
private val CHARACTER_SIZE = 110.dp
private val CHARACTER_TOP_MARGIN = 40.dp
private val CHARACTER_TO_TITLE = 10.dp
private val TITLE_H_MARGIN = 30.dp
private val TITLE_TO_RESTART = 40.dp
private val BUTTON_H_MARGIN = 20.dp
private val RESTART_HEIGHT = 56.dp
private val RESTART_TO_CONTINUE = 16.dp
private val CONTINUE_HEIGHT = 44.dp
private val CORNER_RADIUS = 16.dp

/** iOS 의 dimView 와 동일하게 검정 60% 입니다. */
private val DIM_COLOR = Color.Black.copy(alpha = 0.6f)

/**
 * iOS 의 ExitConfirmModalViewController 에 대응합니다.
 *
 * 이름은 Exit 이지만 실제 동작은 화면을 벗어나는 것이 아니라
 * 대화를 처음부터 다시 시작할지 묻는 것입니다.
 */
@Composable
fun RestartConfirmModal(
    onRestart: () -> Unit,
    onContinue: () -> Unit,
) {
    Dialog(
        onDismissRequest = onContinue,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // 배경을 누르면 닫힙니다. (iOS 의 dimView 탭 제스처)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DIM_COLOR)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onContinue,
                    ),
            )

            val sheetShape = RoundedCornerShape(topStart = SHEET_RADIUS, topEnd = SHEET_RADIUS)

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(SHEET_HEIGHT)
                    .clip(sheetShape)
                    .background(HaruUpColor.AppWhite, sheetShape)
                    // 시트 안쪽 탭이 배경으로 전달되지 않게 막습니다.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(CHARACTER_TOP_MARGIN))

                Image(
                    painter = painterResource(R.drawable.character_curation_close),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(CHARACTER_SIZE),
                )

                Spacer(Modifier.height(CHARACTER_TO_TITLE))

                Text(
                    text = "처음부터 다시 시작할까요?\n지금까지 작성한 답변은 저장되지 않아요.",
                    style = HaruUpType.subtitle1,
                    color = HaruUpColor.AppBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TITLE_H_MARGIN),
                )

                Spacer(Modifier.height(TITLE_TO_RESTART))

                Button(
                    onClick = onRestart,
                    shape = RoundedCornerShape(CORNER_RADIUS),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HaruUpColor.Cta,
                        contentColor = HaruUpColor.AppWhite,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = BUTTON_H_MARGIN)
                        .height(RESTART_HEIGHT),
                ) {
                    Text(text = "처음부터 다시 시작하기", style = HaruUpType.subtitle2)
                }

                Spacer(Modifier.height(RESTART_TO_CONTINUE))

                Box(
                    modifier = Modifier
                        .height(CONTINUE_HEIGHT)
                        .clickable(onClick = onContinue)
                        .padding(horizontal = BUTTON_H_MARGIN),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "계속 진행하기",
                        style = HaruUpType.subtitle2,
                        color = HaruUpColor.Neutral500,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun RestartConfirmModalPreview() {
    HaruUpTheme {
        RestartConfirmModal(onRestart = {}, onContinue = {})
    }
}
