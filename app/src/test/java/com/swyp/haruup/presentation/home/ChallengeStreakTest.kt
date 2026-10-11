package com.swyp.haruup.presentation.home

import com.swyp.haruup.data.model.ChallengeDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * 연속 달성일 계산을 고정합니다.
 *
 * 오늘을 어떻게 다루느냐가 핵심입니다. 아직 오늘 미션을 안 했다고 해서
 * 어제까지 쌓은 숫자가 0 으로 떨어지면 안 됩니다.
 */
class ChallengeStreakTest {

    private val today = LocalDate.of(2026, 10, 6)

    private fun day(date: String, completed: Boolean) = ChallengeDate(date, completed)

    // MARK: - 연속 달성일

    @Test
    fun `어제까지 이어졌으면 오늘을 안 해도 숫자가 유지된다`() {
        val days = listOf(
            day("2026-10-04", true),
            day("2026-10-05", true),
            day("2026-10-06", false),
        )

        assertEquals(2, challengeStreak(days, today))
    }

    @Test
    fun `오늘까지 했으면 오늘도 센다`() {
        val days = listOf(
            day("2026-10-05", true),
            day("2026-10-06", true),
        )

        assertEquals(2, challengeStreak(days, today))
    }

    @Test
    fun `중간에 못 한 날이 있으면 거기서 끊는다`() {
        val days = listOf(
            day("2026-10-01", true),
            day("2026-10-02", true),
            day("2026-10-03", false),
            day("2026-10-04", true),
            day("2026-10-05", true),
            day("2026-10-06", true),
        )

        assertEquals(3, challengeStreak(days, today))
    }

    @Test
    fun `하루도 못 했으면 0 이다`() {
        val days = listOf(day("2026-10-05", false), day("2026-10-06", false))

        assertEquals(0, challengeStreak(days, today))
    }

    @Test
    fun `받은 날짜가 없으면 0 이다`() {
        assertEquals(0, challengeStreak(emptyList(), today))
    }

    // MARK: - 시트의 7칸

    @Test
    fun `처음 달성한 날부터 7칸을 만든다`() {
        val days = listOf(
            day("2026-10-04", false),
            day("2026-10-05", true),
            day("2026-10-06", true),
        )

        val slots = challengeDays(days)

        assertEquals(7, slots.size)
        // 10-05(월) 부터 시작합니다.
        assertEquals("월", slots[0].dayLabel)
        assertEquals(MissionChallengeStatus.COMPLETED, slots[0].status)
        assertEquals(MissionChallengeStatus.COMPLETED, slots[1].status)
    }

    @Test
    fun `서버가 주지 않은 날은 빈 칸으로 둔다`() {
        val days = listOf(day("2026-10-05", true), day("2026-10-06", true))

        val slots = challengeDays(days)

        // 받은 이틀 뒤로는 아직 오지 않은 날이라 비워 둡니다.
        assertTrue(slots.drop(2).all { it.status == MissionChallengeStatus.NONE })
    }

    @Test
    fun `달성한 날이 하루도 없으면 받은 날짜만 보여준다`() {
        val days = listOf(day("2026-10-05", false), day("2026-10-06", false))

        val slots = challengeDays(days)

        assertEquals(2, slots.size)
        assertTrue(slots.all { it.status == MissionChallengeStatus.FAILED })
    }

    @Test
    fun `받은 날짜가 없으면 칸을 만들지 않는다`() {
        assertTrue(challengeDays(emptyList()).isEmpty())
    }
}
