package com.swyp.haruup.presentation.history

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarGridTest {

    @Test
    fun `격자는 항상 월요일에서 시작한다`() {
        listOf("2026-01", "2026-02", "2026-09", "2026-11").forEach { month ->
            val grid = buildCalendarGrid(YearMonth.parse(month))
            assertEquals(month, DayOfWeek.MONDAY, grid.first().date.dayOfWeek)
        }
    }

    @Test
    fun `격자 칸 수는 항상 7의 배수다`() {
        (1..12).forEach { month ->
            val grid = buildCalendarGrid(YearMonth.of(2026, month))
            assertEquals("${month}월", 0, grid.size % 7)
        }
    }

    @Test
    fun `해당 월의 모든 날짜가 빠짐없이 들어간다`() {
        val yearMonth = YearMonth.of(2026, 9)
        val grid = buildCalendarGrid(yearMonth)
        val currentMonthDays = grid.filter { it.isCurrentMonth }.map { it.date.dayOfMonth }

        assertEquals((1..30).toList(), currentMonthDays)
    }

    @Test
    fun `앞뒤 빈 칸은 이전 다음 달 날짜로 채운다`() {
        // 2026-09-01 은 화요일이라 앞에 월요일 한 칸이 붙는다
        val grid = buildCalendarGrid(YearMonth.of(2026, 9))

        assertEquals(LocalDate.of(2026, 8, 31), grid.first().date)
        assertFalse(grid.first().isCurrentMonth)
        assertFalse(grid.last().isCurrentMonth)
    }

    @Test
    fun `월요일로 시작하는 달은 앞에 빈 칸이 없다`() {
        // 2026-06-01 은 월요일
        val grid = buildCalendarGrid(YearMonth.of(2026, 6))

        assertEquals(LocalDate.of(2026, 6, 1), grid.first().date)
        assertTrue(grid.first().isCurrentMonth)
    }

    @Test
    fun `윤년 2월도 모든 날짜가 들어간다`() {
        val grid = buildCalendarGrid(YearMonth.of(2024, 2))
        assertEquals(29, grid.count { it.isCurrentMonth })
    }

    @Test
    fun `완료 개수가 5를 넘어도 마지막 이미지를 쓴다`() {
        assertEquals(calendarStampRes(5), calendarStampRes(9))
    }

    @Test
    fun `월별 통계에서 날짜별 완료 개수를 찾는다`() {
        val summary = MonthlyMissionSummary(
            dailyCounts = listOf(
                DailyMissionCount(LocalDate.of(2026, 9, 1), 3),
                DailyMissionCount(LocalDate.of(2026, 9, 2), 1),
            ),
        )

        assertEquals(3, summary.completedCount(LocalDate.of(2026, 9, 1)))
        assertEquals(1, summary.completedCount(LocalDate.of(2026, 9, 2)))
        // 기록이 없는 날은 0
        assertEquals(0, summary.completedCount(LocalDate.of(2026, 9, 3)))
    }

    @Test
    fun `이번 달에서는 다음 달로 갈 수 없다`() {
        val state = HistoryUiState(yearMonth = YearMonth.now())
        assertFalse(state.canGoNext)
        assertTrue(state.copy(yearMonth = YearMonth.now().minusMonths(1)).canGoNext)
    }
}
