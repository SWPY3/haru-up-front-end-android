package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.component.BottomSheetDialog
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

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
