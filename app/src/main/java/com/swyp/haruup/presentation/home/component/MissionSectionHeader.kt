package com.swyp.haruup.presentation.home.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType

private val H_MARGIN = 20.dp
private val TITLE_TO_INFO = 4.dp
private val TITLE_TO_SUBTITLE = 4.dp
private val SECTION_BOTTOM = 15.dp
private val INFO_ICON_SIZE = 24.dp
private val TOOLTIP_RADIUS = 8.dp
private val TOOLTIP_H_PADDING = 12.dp
private val TOOLTIP_V_PADDING = 8.dp
private val TOOLTIP_GAP = 7.dp

/**
 * 오늘의 미션 섹션 머리말. iOS 의 HomeSectionHeaderView 에 대응합니다.
 * 물음표를 누르면 안내 말풍선이 열리고 닫힙니다.
 */
@Composable
fun MissionSectionHeader(
    isTooltipVisible: Boolean,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {

        AnimatedVisibility(
            visible = isTooltipVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column {
                Text(
                    text = "오늘의 미션은 자정이 지나면 사라져요.",
                    style = HaruUpType.caption2,
                    color = HaruUpColor.AppWhite,
                    modifier = Modifier
                        .padding(start = H_MARGIN)
                        .clip(RoundedCornerShape(TOOLTIP_RADIUS))
                        .background(HaruUpColor.Neutral1000, RoundedCornerShape(TOOLTIP_RADIUS))
                        .padding(
                            horizontal = TOOLTIP_H_PADDING,
                            vertical = TOOLTIP_V_PADDING,
                        ),
                )
                Spacer(Modifier.height(TOOLTIP_GAP))
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = H_MARGIN),
            horizontalArrangement = Arrangement.spacedBy(TITLE_TO_INFO),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(text = "오늘의 미션", style = HaruUpType.title3, color = HaruUpColor.AppBlack)

            Image(
                painter = painterResource(R.drawable.ic_info),
                contentDescription = "오늘의 미션 안내",
                modifier = Modifier
                    .size(INFO_ICON_SIZE)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onInfoClick,
                    ),
            )
        }

        Spacer(Modifier.height(TITLE_TO_SUBTITLE))

        Text(
            text = "미션은 하루 최대 5개까지 선택할 수 있어요.",
            style = HaruUpType.body3,
            color = HaruUpColor.Neutral500,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(SECTION_BOTTOM))
    }
}
