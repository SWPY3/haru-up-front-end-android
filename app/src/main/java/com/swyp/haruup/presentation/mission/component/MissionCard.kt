package com.swyp.haruup.presentation.mission.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mission.MissionItem

private val CARD_RADIUS = 16.dp
private val CARD_H_MARGIN = 20.dp
private val CARD_BOTTOM_SPACING = 12.dp
private val CHECKBOX_SIZE = 24.dp
private val CHECKBOX_TOP = 24.dp
private val CHECKBOX_LEADING = 20.dp
private val CHECKBOX_TO_CONTENT = 10.dp
private val CONTENT_TOP = 20.dp
private val CONTENT_BOTTOM = 30.dp
private val CONTENT_TRAILING = 20.dp
private val CONTENT_SPACING = 12.dp
private val BADGE_SPACING = 7.dp

/** 5개를 다 골랐을 때 고를 수 없는 카드의 투명도. iOS 와 동일하게 0.7 입니다. */
private const val DISABLED_ALPHA = 0.7f

/**
 * iOS 의 TodayMissionTableViewCell 에 대응합니다.
 */
@Composable
fun MissionCard(
    mission: MissionItem,
    isSelected: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderWidth by animateDpAsState(if (isSelected) 1.5.dp else 1.dp, label = "border_width")
    val borderColor by animateColorAsState(
        if (isSelected) HaruUpColor.PrimaryBlue500 else HaruUpColor.Neutral50,
        label = "border_color",
    )

    val shape = RoundedCornerShape(CARD_RADIUS)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = CARD_H_MARGIN)
            .padding(bottom = CARD_BOTTOM_SPACING)
            .alpha(if (isDisabled) DISABLED_ALPHA else 1f)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .border(BorderStroke(borderWidth, borderColor), shape)
            .clickable(enabled = !isDisabled, onClick = onClick),
    ) {
        Image(
            painter = painterResource(
                if (isSelected) R.drawable.ic_mission_checkbox_selected
                else R.drawable.ic_mission_checkbox_unselected
            ),
            contentDescription = null,
            modifier = Modifier
                .padding(start = CHECKBOX_LEADING, top = CHECKBOX_TOP)
                .size(CHECKBOX_SIZE),
        )

        Spacer(Modifier.width(CHECKBOX_TO_CONTENT))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = CONTENT_TOP, bottom = CONTENT_BOTTOM, end = CONTENT_TRAILING),
            verticalArrangement = Arrangement.spacedBy(CONTENT_SPACING),
        ) {
            Text(
                text = mission.content,
                style = HaruUpType.subtitle2,
                color = if (isDisabled) HaruUpColor.Neutral700 else HaruUpColor.AppBlack,
            )

            if (!mission.description.isNullOrEmpty()) {
                Text(
                    text = mission.description,
                    style = HaruUpType.body4,
                    color = HaruUpColor.Neutral700,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(BADGE_SPACING),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MissionBadge(
                    text = mission.difficulty.label,
                    background = mission.difficulty.backgroundColor,
                    textColor = mission.difficulty.textColor,
                    horizontalPadding = 6.dp,
                )
                MissionBadge(
                    text = "${mission.expEarned} EXP",
                    background = HaruUpColor.Neutral10,
                    textColor = HaruUpColor.Neutral600,
                    horizontalPadding = 10.dp,
                )
            }
        }
    }
}

/** 난이도 배지와 경험치 배지. 둘 다 모서리 5 에 좌우 여백만 다릅니다. */
@Composable
private fun MissionBadge(
    text: String,
    background: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    horizontalPadding: androidx.compose.ui.unit.Dp,
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
