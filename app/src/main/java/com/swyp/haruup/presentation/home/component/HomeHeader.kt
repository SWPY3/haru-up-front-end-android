package com.swyp.haruup.presentation.home.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.home.HomeMemberInfo

private val H_MARGIN = 20.dp
private val ACHIEVEMENT_V_PADDING = 3.dp
private val ACHIEVEMENT_SPACING = 8.dp
private val ACHIEVEMENT_TO_BUBBLE = 36.dp
private val BUBBLE_TO_CHARACTER = 20.dp
private val CHARACTER_SIZE = 180.dp
private val CHARACTER_TO_INFO = 27.dp
private val INFO_SPACING = 8.dp
private val LEVEL_BADGE_RADIUS = 8.dp
private val LEVEL_BADGE_H_PADDING = 8.dp
private val LEVEL_BADGE_V_PADDING = 2.dp
private val LEVEL_TO_NAME = 8.dp
private val EXP_BAR_HEIGHT = 12.dp
private val EXP_BAR_TO_LABEL = 8.dp
private val EXP_LABEL_SPACING = 3.dp
private val HEADER_BOTTOM_PADDING = 8.dp

/**
 * 홈 상단 영역. iOS 의 HomeHeaderView 에 대응합니다.
 *
 * 배경은 시간대에 따라 낮/밤 이미지가 바뀌고,
 * 캐릭터나 말풍선을 누르면 응원 문구가 다음 것으로 넘어갑니다.
 */
@Composable
fun HomeHeader(
    memberInfo: HomeMemberInfo,
    bubbleText: String,
    challengeDay: Int,
    isDaytime: Boolean,
    onBubbleClick: () -> Unit,
    onChallengeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {

        Crossfade(targetState = isDaytime, label = "home_background") { daytime ->
            Image(
                painter = painterResource(
                    if (daytime) R.drawable.image_home_background_day
                    else R.drawable.image_home_background_night
                ),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = HEADER_BOTTOM_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AchievementRow(
                challengeDay = challengeDay,
                onClick = onChallengeClick,
                modifier = Modifier.align(Alignment.End),
            )

            Spacer(Modifier.height(ACHIEVEMENT_TO_BUBBLE))

            SpeechBubble(
                text = bubbleText,
                modifier = Modifier
                    .padding(horizontal = H_MARGIN)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBubbleClick,
                    ),
            )

            Spacer(Modifier.height(BUBBLE_TO_CHARACTER))

            Box(contentAlignment = Alignment.BottomCenter) {
                Image(
                    painter = painterResource(R.drawable.character_shadow),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(bottom = 6.dp),
                )

                Crossfade(targetState = memberInfo.characterImageRes, label = "home_character") { res ->
                    Image(
                        painter = painterResource(res),
                        contentDescription = memberInfo.characterDisplayName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(CHARACTER_SIZE)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onBubbleClick,
                            ),
                    )
                }
            }

            Spacer(Modifier.height(CHARACTER_TO_INFO))

            LevelAndName(memberInfo)

            Spacer(Modifier.height(INFO_SPACING))

            ExpBar(memberInfo)
        }
    }
}

@Composable
private fun AchievementRow(
    challengeDay: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(end = H_MARGIN, top = ACHIEVEMENT_V_PADDING, bottom = ACHIEVEMENT_V_PADDING)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        horizontalArrangement = Arrangement.spacedBy(ACHIEVEMENT_SPACING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "연속 미션 달성일", style = HaruUpType.body3, color = HaruUpColor.AppBlack)

        Image(
            painter = painterResource(
                // 하루도 이어가지 못했으면 꺼진 아이콘을 씁니다.
                if (challengeDay == 0) R.drawable.ic_fire_inactive else R.drawable.ic_fire_active
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )

        Text(
            text = challengeDay.toString(),
            style = HaruUpType.subtitle2,
            color = HaruUpColor.AppBlack,
        )
    }
}

@Composable
private fun LevelAndName(memberInfo: HomeMemberInfo) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LEVEL_TO_NAME),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Lv. ${memberInfo.level}",
            style = HaruUpType.level,
            color = HaruUpColor.AppWhite,
            modifier = Modifier
                .clip(RoundedCornerShape(LEVEL_BADGE_RADIUS))
                .background(HaruUpColor.Neutral400, RoundedCornerShape(LEVEL_BADGE_RADIUS))
                .padding(
                    horizontal = LEVEL_BADGE_H_PADDING,
                    vertical = LEVEL_BADGE_V_PADDING,
                ),
        )

        Text(
            text = memberInfo.characterDisplayName,
            style = HaruUpType.subtitle2,
            color = HaruUpColor.AppBlack,
        )
    }
}

@Composable
private fun ExpBar(memberInfo: HomeMemberInfo) {
    Column(modifier = Modifier.padding(horizontal = H_MARGIN)) {
        LinearProgressIndicator(
            progress = { memberInfo.expProgress },
            color = HaruUpColor.PrimaryBlue700,
            trackColor = HaruUpColor.AppWhite,
            drawStopIndicator = {},
            gapSize = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(EXP_BAR_HEIGHT)
                .clip(RoundedCornerShape(EXP_BAR_HEIGHT / 2))
                .border(
                    width = 1.dp,
                    color = HaruUpColor.Neutral50,
                    shape = RoundedCornerShape(EXP_BAR_HEIGHT / 2),
                ),
        )

        Spacer(Modifier.height(EXP_BAR_TO_LABEL))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EXP_LABEL_SPACING, Alignment.End),
        ) {
            Text("${memberInfo.currentExp}", style = HaruUpType.caption1, color = HaruUpColor.PrimaryBlue700)
            Text("/", style = HaruUpType.caption1, color = HaruUpColor.Neutral500)
            Text("${memberInfo.maxExp}", style = HaruUpType.caption1, color = HaruUpColor.Neutral500)
            Text("EXP", style = HaruUpType.caption1, color = HaruUpColor.Neutral500)
        }
    }
}
