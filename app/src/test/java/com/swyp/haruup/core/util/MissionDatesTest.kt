package com.swyp.haruup.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * 미션 API 에 보낼 날짜 범위를 고정합니다.
 * 범위가 어긋나면 연속 달성일과 성장 차트가 조용히 다른 값을 보여 줍니다.
 */
class MissionDatesTest {

    @Test
    fun `연속 달성일은 오늘 포함 7일을 조회한다`() {
        val (start, end) = MissionDates.challengeRange(LocalDate.of(2026, 10, 6))

        assertEquals("2026-09-30", start)
        assertEquals("2026-10-06", end)
    }

    @Test
    fun `성장 차트는 이번 달 포함 6개월을 조회한다`() {
        val (start, end) = MissionDates.growthRange(LocalDate.of(2026, 10, 6))

        assertEquals("2026-05", start)
        assertEquals("2026-10", end)
    }

    @Test
    fun `성장 차트는 서비스 시작 전까지 거슬러 가지 않는다`() {
        // 2026-02 에서 6개월을 거슬러 가면 2025-09 지만, 그 전에는 데이터가 없습니다.
        val (start, end) = MissionDates.growthRange(LocalDate.of(2026, 2, 1))

        assertEquals("2025-12", start)
        assertEquals("2026-02", end)
    }

    @Test
    fun `날짜 문자열을 되돌려 읽는다`() {
        val date = LocalDate.of(2026, 1, 5)

        assertEquals("2026-01-05", MissionDates.format(date))
        assertEquals(date, MissionDates.parseOrNull("2026-01-05"))
    }

    @Test
    fun `잘못된 날짜는 null 로 둔다`() {
        assertEquals(null, MissionDates.parseOrNull(""))
        assertEquals(null, MissionDates.parseOrNull("2026-13-01"))
    }

    @Test
    fun `요일은 한 글자로 보여준다`() {
        // 2026-10-06 은 화요일입니다.
        assertEquals("화", MissionDates.dayLabel(LocalDate.of(2026, 10, 6)))
    }
}
