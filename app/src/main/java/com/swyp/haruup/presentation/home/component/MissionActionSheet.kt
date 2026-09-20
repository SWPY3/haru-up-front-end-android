package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val SHEET_RADIUS = 32.dp
private val SHEET_H_MARGIN = 20.dp
private val TITLE_TOP = 28.dp
private val TITLE_TO_BUTTONS = 16.dp
private val ACTION_HEIGHT = 54.dp
private val ACTION_ICON_SIZE = 24.dp
private val ACTION_ICON_TO_TEXT = 12.dp
private val ACTION_LEADING = 10.dp
private val SHEET_BOTTOM = 40.dp

/**
 * 미션 카드의 상세 버튼을 눌렀을 때 뜨는 시트.
 * iOS 의 MissionBottomSheetViewController 에 대응합니다.
 */
@Composable
fun MissionActionSheet(
    missionTitle: String,
    onCompleteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    BottomSheetDialog(onDismiss = onDismiss) {
        Spacer(Modifier.height(TITLE_TOP))

        Text(
            text = missionTitle,
            style = HaruUpType.subtitle1,
            color = HaruUpColor.AppBlack,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SHEET_H_MARGIN),
        )

        Spacer(Modifier.height(TITLE_TO_BUTTONS))

        ActionRow(
            text = "미션 완료",
            iconRes = R.drawable.ic_mission_complete,
            onClick = onCompleteClick,
        )
        ActionRow(
            text = "미션 삭제",
            iconRes = R.drawable.ic_mission_delete,
            onClick = onDeleteClick,
        )

        Spacer(Modifier.height(SHEET_BOTTOM))
    }
}

@Composable
private fun ActionRow(
    text: String,
    iconRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ACTION_HEIGHT)
            .clickable(onClick = onClick)
            .padding(start = SHEET_H_MARGIN + ACTION_LEADING, end = SHEET_H_MARGIN),
        horizontalArrangement = Arrangement.spacedBy(ACTION_ICON_TO_TEXT),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(ACTION_ICON_SIZE),
        )
        Text(text = text, style = HaruUpType.body1, color = HaruUpColor.Neutral1000)
    }
}

/**
 * 아래에서 올라오는 흰 시트의 공통 껍데기입니다.
 * 딤 영역을 누르면 닫힙니다.
 */
@Composable
fun BottomSheetDialog(
    onDismiss: () -> Unit,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
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
                modifier = Modifier
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

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun MissionActionSheetPreview() {
    HaruUpTheme {
        MissionActionSheet(
            missionTitle = "영어 회화 유튜브 강의 10분 보기",
            onCompleteClick = {}, onDeleteClick = {}, onDismiss = {},
        )
    }
}
