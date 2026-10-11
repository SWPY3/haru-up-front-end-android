package com.swyp.haruup.presentation.history.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mission.MissionItem

private val CARD_RADIUS = 24.dp
private val CARD_PADDING = 24.dp
private val TITLE_TO_CONTENT = 0.dp
private val ITEM_V_PADDING = 18.dp
private val TITLE_TO_BADGE = 10.dp
private val BADGE_SPACING = 8.dp
private val EMPTY_ICON_SIZE = 56.dp
private val EMPTY_ICON_TOP = 12.dp
private val EMPTY_ICON_TO_TEXT = 2.dp

/**
 * 선택한 날짜에 완료한 미션 카드.
 * iOS 의 missionCardView 영역에 대응합니다.
 */
@Composable
fun DailyMissionCard(
    title: String,
    missions: List<MissionItem>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .padding(CARD_PADDING),
    ) {
        Text(text = title, style = HaruUpType.subtitle1, color = HaruUpColor.AppBlack)

        Spacer(Modifier.height(TITLE_TO_CONTENT))

        if (missions.isEmpty()) {
            EmptyMissions()
        } else {
            missions.forEachIndexed { index, mission ->
                MissionRow(mission)

                // 마지막 항목 뒤에는 구분선을 두지 않습니다.
                if (index < missions.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(HaruUpColor.Neutral50),
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionRow(mission: MissionItem) {
    Column(modifier = Modifier.padding(vertical = ITEM_V_PADDING)) {
        Text(
            text = mission.content,
            style = HaruUpType.body1,
            color = HaruUpColor.Neutral900,
        )

        Spacer(Modifier.height(TITLE_TO_BADGE))

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
}

@Composable
private fun EmptyMissions() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(EMPTY_ICON_TOP))

        Image(
            painter = painterResource(R.drawable.ic_calendar_empty),
            contentDescription = null,
            modifier = Modifier.size(EMPTY_ICON_SIZE),
        )

        Spacer(Modifier.height(EMPTY_ICON_TO_TEXT))

        Text(
            text = "완료한 미션이 없어요",
            style = HaruUpType.subtitle2,
            color = HaruUpColor.Neutral600,
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
