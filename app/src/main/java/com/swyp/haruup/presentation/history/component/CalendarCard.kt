package com.swyp.haruup.presentation.history.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.history.CalendarDay
import com.swyp.haruup.presentation.history.MonthlyMissionSummary
import com.swyp.haruup.presentation.history.calendarStampRes
import java.time.LocalDate
import java.time.YearMonth

private val CARD_RADIUS = 24.dp
private val CARD_PADDING = 20.dp
private val NAV_BUTTON_SIZE = 44.dp
private val NAV_ICON_SIZE = 24.dp
private val HEADER_TO_STATS = 20.dp
private val STATS_SPACING = 20.dp
private val STATS_TO_WEEKDAYS = 20.dp
private val WEEKDAY_ROW_HEIGHT = 40.dp
private val STAMP_SIZE = 32.dp

/** 월요일 시작. iOS CalendarHeaderView 와 같은 순서입니다. */
private val WEEKDAYS = listOf("월", "화", "수", "목", "금", "토", "일")

/**
 * 기록 탭의 캘린더 카드. iOS 의 calendarCardView 영역에 대응합니다.
 */
@Composable
fun CalendarCard(
    yearMonth: YearMonth,
    monthLabel: String,
    days: List<CalendarDay>,
    selectedDate: LocalDate,
    summary: MonthlyMissionSummary,
    canGoNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)
    val today = LocalDate.now()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .padding(CARD_PADDING),
    ) {
        MonthNavigation(
            monthLabel = monthLabel,
            canGoNext = canGoNext,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
        )

        Spacer(Modifier.height(HEADER_TO_STATS))

        Row(horizontalArrangement = Arrangement.spacedBy(STATS_SPACING)) {
            StatItem(
                title = "출석일",
                value = summary.totalCompletedDays,
                unit = "일",
                modifier = Modifier.weight(1f),
            )
            StatItem(
                title = "완료한 미션",
                value = summary.totalMissionCount,
                unit = "개",
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(STATS_TO_WEEKDAYS))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(WEEKDAY_ROW_HEIGHT),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WEEKDAYS.forEach { label ->
                Text(
                    text = label,
                    style = HaruUpType.calendarWeek,
                    color = HaruUpColor.Neutral700,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // 격자는 항상 7칸씩이라 LazyGrid 대신 Row 를 쌓습니다.
        days.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        isSelected = day.date == selectedDate,
                        isToday = day.date == today,
                        completedCount = summary.completedCount(day.date),
                        onClick = { onDayClick(day) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthNavigation(
    monthLabel: String,
    canGoNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavButton(
            iconRes = R.drawable.ic_calendar_chevron_left,
            contentDescription = "이전 달",
            enabled = true,
            onClick = onPreviousClick,
        )

        Text(
            text = monthLabel,
            style = HaruUpType.subtitle1,
            color = HaruUpColor.AppBlack,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )

        NavButton(
            iconRes = R.drawable.ic_calendar_chevron_right,
            contentDescription = "다음 달",
            enabled = canGoNext,
            onClick = onNextClick,
        )
    }
}

@Composable
private fun NavButton(
    iconRes: Int,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(NAV_BUTTON_SIZE)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            // 더 갈 수 없는 방향은 흐리게 둡니다.
            modifier = Modifier
                .size(NAV_ICON_SIZE)
                .alpha(if (enabled) 1f else 0.3f),
        )
    }
}

@Composable
private fun StatItem(
    title: String,
    value: Int,
    unit: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = title, style = HaruUpType.body4, color = HaruUpColor.Neutral900)

        Spacer(Modifier.height(2.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(text = "$value", style = HaruUpType.head2, color = HaruUpColor.Cta)
            Text(text = unit, style = HaruUpType.caption2, color = HaruUpColor.Neutral600)
        }
    }
}

@Composable
private fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    isToday: Boolean,
    completedCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasStamp = day.isCurrentMonth && completedCount > 0

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        // 선택 표시가 가장 아래에 깔립니다.
        if (isSelected && day.isCurrentMonth) {
            Box(
                modifier = Modifier
                    .size(STAMP_SIZE)
                    .clip(CircleShape)
                    .background(HaruUpColor.Calendar.Selected, CircleShape),
            )
        }

        if (isToday && day.isCurrentMonth) {
            Image(
                painter = painterResource(R.drawable.ic_calendar_today),
                contentDescription = null,
                modifier = Modifier.size(STAMP_SIZE),
            )
        }

        if (hasStamp) {
            Image(
                painter = painterResource(calendarStampRes(completedCount)),
                contentDescription = null,
                modifier = Modifier.size(STAMP_SIZE),
            )
        }

        Text(
            text = "${day.date.dayOfMonth}",
            style = HaruUpType.calendarDay,
            color = when {
                !day.isCurrentMonth -> HaruUpColor.Neutral200
                hasStamp -> HaruUpColor.Calendar.DayWhite
                else -> HaruUpColor.AppBlack
            },
        )
    }
}
