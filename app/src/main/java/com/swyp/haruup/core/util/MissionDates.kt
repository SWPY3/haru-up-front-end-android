package com.swyp.haruup.core.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 미션 API 가 쓰는 날짜 문자열을 만듭니다. iOS 의 DateHelper 와 MissionService 의 날짜 헬퍼에 대응합니다.
 */
object MissionDates {

    val DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val MONTH: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    /** 연속 달성일은 오늘 포함 7일을 봅니다. */
    private const val CHALLENGE_DAYS = 7

    /** 성장 차트는 이번 달 포함 6개월을 봅니다. */
    private const val GROWTH_MONTHS = 6

    /** 서비스 시작 전 달은 조회하지 않습니다. (iOS 와 같은 하한) */
    val EARLIEST_MONTH: YearMonth = YearMonth.of(2025, 12)

    fun format(date: LocalDate): String = date.format(DATE)

    fun format(month: YearMonth): String = month.format(MONTH)

    fun parseOrNull(date: String): LocalDate? = runCatching { LocalDate.parse(date, DATE) }.getOrNull()

    /** 예: "월" */
    fun dayLabel(date: LocalDate): String =
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

    /** 오늘 포함 7일 (시작일, 종료일) */
    fun challengeRange(today: LocalDate = LocalDate.now()): Pair<String, String> =
        format(today.minusDays((CHALLENGE_DAYS - 1).toLong())) to format(today)

    /**
     * 이번 달 포함 6개월 (시작월, 종료월).
     * 서비스 시작 전까지 거슬러 가지 않도록 하한을 둡니다.
     */
    fun growthRange(today: LocalDate = LocalDate.now()): Pair<String, String> {
        val end = YearMonth.from(today)
        val start = maxOf(end.minusMonths((GROWTH_MONTHS - 1).toLong()), EARLIEST_MONTH)

        return format(start) to format(end)
    }
}
