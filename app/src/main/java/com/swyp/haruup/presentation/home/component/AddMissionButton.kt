package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType

private val H_MARGIN = 20.dp
private val CARD_RADIUS = 15.dp
private val BUTTON_RADIUS = 16.dp
private val CARD_PADDING = 20.dp
private val CARD_BOTTOM_PADDING = 26.dp
private val MESSAGE_TO_BUTTON = 16.dp
private val EMPTY_BUTTON_HEIGHT = 45.dp
private val STANDALONE_BUTTON_HEIGHT = 56.dp
private val ICON_SIZE = 18.dp
private val ICON_TO_TEXT = 6.dp

/**
 * 미션을 하나도 고르지 않았을 때 보이는 카드.
 * iOS 의 EmptyMissionCell 에 대응합니다.
 */
@Composable
fun EmptyMissionCard(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = H_MARGIN)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .padding(
                start = CARD_PADDING,
                end = CARD_PADDING,
                top = CARD_PADDING,
                bottom = CARD_BOTTOM_PADDING,
            ),
    ) {
        Text(
            text = "오늘의 미션을 아직 선택하지 않았어요.",
            style = HaruUpType.subtitle2,
            color = HaruUpColor.Neutral500,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(MESSAGE_TO_BUTTON))

        AddMissionButton(onClick = onAddClick, height = EMPTY_BUTTON_HEIGHT)
    }
}

/**
 * 미션 목록 아래에 붙는 추가 버튼.
 * iOS 의 AddMissionTableViewCell 에 대응합니다.
 */
@Composable
fun AddMissionRow(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AddMissionButton(
        onClick = onAddClick,
        height = STANDALONE_BUTTON_HEIGHT,
        modifier = modifier.padding(horizontal = H_MARGIN),
    )
}

@Composable
private fun AddMissionButton(
    onClick: () -> Unit,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(BUTTON_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = HaruUpColor.Cta,
            contentColor = HaruUpColor.AppWhite,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ICON_TO_TEXT, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier.size(ICON_SIZE),
            )
            Text(text = "미션 추가하기", style = HaruUpType.body2)
        }
    }
}
