package com.swyp.haruup.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swyp.haruup.core.designsystem.HaruUpColor

private val SHEET_RADIUS = 32.dp

/**
 * 아래에서 올라오는 흰 시트의 공통 껍데기입니다.
 * 딤 영역을 누르면 닫힙니다.
 *
 * [modifier] 는 시트 본체에 붙습니다. 높이를 정해야 하는 시트에서 쓰세요.
 */
@Composable
fun BottomSheetDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HaruUpColor.Background.BottomSheet)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
            )

            val shape = RoundedCornerShape(topStart = SHEET_RADIUS, topEnd = SHEET_RADIUS)

            Column(
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .clip(shape)
                    .background(HaruUpColor.AppWhite, shape)
                    .navigationBarsPadding()
                    // 시트 안쪽 탭이 딤으로 전달되지 않게 막습니다.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
                content = content,
            )
        }
    }
}
