package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mission.MissionItem

private val CARD_RADIUS = 16.dp
private val H_MARGIN = 20.dp
private val CARD_SPACING = 16.dp
private val CARD_PADDING = 20.dp
private val CONTENT_SPACING = 10.dp
private val BADGE_SPACING = 7.dp
private val SETTING_ICON_SIZE = 24.dp
private val CONTENT_TO_SETTING = 20.dp

/**
 * 홈의 오늘의 미션 카드. iOS 의 MissionTableViewCell 에 대응합니다.
 *
 * 완료한 미션은 제목에 취소선을 긋고 색을 낮추며, 상세 버튼을 막습니다.
 */
@Composable
fun HomeMissionCard(
    mission: MissionItem,
    isCompleted: Boolean,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = H_MARGIN)
            .padding(bottom = CARD_SPACING)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .padding(CARD_PADDING),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(CONTENT_SPACING),
        ) {
            Text(
                text = mission.content,
                style = HaruUpType.subtitle2,
                color = if (isCompleted) HaruUpColor.Neutral300 else HaruUpColor.AppBlack,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
            )

            if (!mission.description.isNullOrEmpty()) {
                Text(
                    text = mission.description,
                    style = HaruUpType.body4,
                    color = if (isCompleted) HaruUpColor.Neutral200 else HaruUpColor.Neutral700,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(BADGE_SPACING),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Badge(
                    text = mission.difficulty.label,
                    background = mission.difficulty.backgroundColor,
                    textColor = mission.difficulty.textColor,
                    horizontalPadding = 6.dp,
                )
                Badge(
                    text = "${mission.expEarned} EXP",
                    background = HaruUpColor.Neutral10,
                    textColor = HaruUpColor.Neutral600,
                    horizontalPadding = 10.dp,
                )
            }
        }

        Spacer(Modifier.width(CONTENT_TO_SETTING))

        Image(
            painter = painterResource(R.drawable.ic_mission_info),
            contentDescription = "미션 상세",
            modifier = Modifier
                .size(SETTING_ICON_SIZE)
                .clickable(
                    // 완료한 미션은 상세를 열 수 없습니다. (iOS settingButton.isEnabled)
                    enabled = !isCompleted,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onSettingClick,
                ),
        )
    }
}

@Composable
private fun Badge(
    text: String,
    background: Color,
    textColor: Color,
    horizontalPadding: Dp,
) {
    val shape = RoundedCornerShape(5.dp)

    Text(
        text = text,
        style = HaruUpType.difficulty,
        color = textColor,
        modifier = Modifier
            .clip(shape)
            .background(background, shape)
            .padding(horizontal = horizontalPadding),
    )
}
