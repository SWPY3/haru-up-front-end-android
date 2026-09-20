package com.swyp.haruup.presentation.history

import androidx.annotation.DrawableRes
import com.swyp.haruup.R
import java.time.LocalDate
import java.time.YearMonth

/**
 * 캘린더 한 칸. iOS 의 CalendarDay 에 대응합니다.
 * 앞뒤 달 날짜도 격자를 채우기 위해 함께 들어옵니다.
 */
data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
)

/**
 * 하루에 완료한 미션 개수. iOS 의 DailyMission 에 대응합니다.
 * 홈의 연속 달성 시트에 쓰는 DailyMission 과 이름이 겹쳐 여기서는 Count 를 붙였습니다.
 */
data class DailyMissionCount(
    val date: LocalDate,
    val completedCount: Int,
)

/** 월별 통계. iOS 의 MonthlyMissionSummary 에 대응합니다. */
data class MonthlyMissionSummary(
    val dailyCounts: List<DailyMissionCount> = emptyList(),
    /** 이번 달 완료한 미션 수 */
    val totalMissionCount: Int = 0,
    /** 이번 달 미션을 하나라도 완료한 날 수 */
    val totalCompletedDays: Int = 0,
) {
    private val countByDate: Map<LocalDate, Int> =
        dailyCounts.associate { it.date to it.completedCount }

    fun completedCount(date: LocalDate): Int = countByDate[date] ?: 0
}

/**
 * 완료 개수에 맞는 캘린더 도장 이미지입니다.
 * 서버가 5를 넘는 값을 내려줘도 마지막 이미지를 쓰도록 잘라냅니다.
 */
@DrawableRes
fun calendarStampRes(completedCount: Int): Int = when (completedCount.coerceIn(1, 5)) {
    1 -> R.drawable.ic_calendar_clear_1
    2 -> R.drawable.ic_calendar_clear_2
    3 -> R.drawable.ic_calendar_clear_3
    4 -> R.drawable.ic_calendar_clear_4
    else -> R.drawable.ic_calendar_clear_5
}

/**
 * 월요일 시작 기준으로 [yearMonth] 격자를 만듭니다.
 * 앞뒤 빈 칸은 이전·다음 달 날짜로 채웁니다.
 */
fun buildCalendarGrid(yearMonth: YearMonth): List<CalendarDay> {
    val firstDay = yearMonth.atDay(1)

    // DayOfWeek 는 월=1 이라 그대로 빼면 앞쪽 빈 칸 수가 됩니다.
    val leadingCount = firstDay.dayOfWeek.value - 1
    val start = firstDay.minusDays(leadingCount.toLong())

    val totalCells = ((leadingCount + yearMonth.lengthOfMonth() + 6) / 7) * 7

    return (0 until totalCells).map { offset ->
        val date = start.plusDays(offset.toLong())
        CalendarDay(date = date, isCurrentMonth = YearMonth.from(date) == yearMonth)
    }
}
