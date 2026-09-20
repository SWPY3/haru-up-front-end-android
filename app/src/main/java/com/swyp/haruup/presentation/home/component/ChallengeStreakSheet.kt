package com.swyp.haruup.presentation.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.home.DailyMission
import com.swyp.haruup.presentation.home.MissionChallengeStatus

private val ICON_TOP = 62.dp
private val ICON_TO_TEXT = 12.dp
private val TEXT_TO_DAYS = 28.dp
private val DAYS_H_MARGIN = 33.dp
private val DAYS_TO_BUTTON = 40.dp
private val BUTTON_H_MARGIN = 20.dp
private val BUTTON_HEIGHT = 56.dp
private val BUTTON_BOTTOM = 45.dp
private val CORNER_RADIUS = 16.dp

/**
 * 연속 미션 달성 시트. iOS 의 MissionDayBottomSheetViewController 에 대응합니다.
 */
@Composable
fun ChallengeStreakSheet(
    challengeDay: Int,
    dailyMissions: List<DailyMission>,
    onConfirm: () -> Unit,
) {
    BottomSheetDialog(onDismiss = onConfirm) {
        Spacer(Modifier.height(ICON_TOP))

        Image(
            painter = painterResource(R.drawable.ic_challenge_fire),
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(Modifier.height(ICON_TO_TEXT))

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(text = "연속 미션 달성", style = HaruUpType.title3, color = HaruUpColor.AppBlack)

            Text(
                text = buildAnnotatedString {
                    // 숫자만 크고 진하게 강조합니다.
                    withStyle(SpanStyle(color = HaruUpColor.PrimaryBlue700)) {
                        append("$challengeDay")
                    }
                    withStyle(SpanStyle(color = HaruUpColor.AppBlack)) { append(" 일차") }
                },
                style = HaruUpType.title2,
                textAlign = TextAlign.Center,
            )

            Text(
                text = if (challengeDay == 1) "시작이 반이에요. 화이팅!" else "대단해요! 잘하고 있어요.",
                style = HaruUpType.body4,
                color = HaruUpColor.Neutral700,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(TEXT_TO_DAYS))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DAYS_H_MARGIN),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            dailyMissions.forEach { daily ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(daily.status.iconRes),
                        contentDescription = null,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = daily.dayLabel,
                        style = HaruUpType.caption3,
                        color = HaruUpColor.Neutral500,
                    )
                }
            }
        }

        Spacer(Modifier.height(DAYS_TO_BUTTON))

        Button(
            onClick = onConfirm,
            shape = RoundedCornerShape(CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = HaruUpColor.Cta,
                contentColor = HaruUpColor.AppWhite,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = BUTTON_H_MARGIN)
                .height(BUTTON_HEIGHT),
        ) {
            Text(text = "확인", style = HaruUpType.subtitle2)
        }

        Spacer(Modifier.height(BUTTON_BOTTOM))
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChallengeStreakSheetPreview() {
    HaruUpTheme {
        ChallengeStreakSheet(
            challengeDay = 3,
            dailyMissions = listOf(
                DailyMission("월", MissionChallengeStatus.COMPLETED),
                DailyMission("화", MissionChallengeStatus.COMPLETED),
                DailyMission("수", MissionChallengeStatus.COMPLETED),
                DailyMission("목", MissionChallengeStatus.NONE),
                DailyMission("금", MissionChallengeStatus.NONE),
                DailyMission("토", MissionChallengeStatus.FAILED),
                DailyMission("일", MissionChallengeStatus.NONE),
            ),
            onConfirm = {},
        )
    }
}
