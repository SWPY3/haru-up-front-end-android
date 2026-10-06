package com.swyp.haruup.presentation.home

import com.swyp.haruup.core.util.MissionDates
import com.swyp.haruup.data.model.ChallengeDate
import java.time.LocalDate

/** 연속 달성 시트에 보여 줄 칸 수입니다. */
private const val CHALLENGE_SLOTS = 7

/**
 * 연속으로 며칠 달성했는지 셉니다. iOS HomeViewModel 의 challengeCount 와 같은 규칙입니다.
 *
 * 최근 날짜부터 거슬러 올라가며 세다가 못 한 날을 만나면 멈춥니다.
 * 다만 오늘은 예외로, 아직 안 했더라도 연속이 끊긴 것으로 보지 않고 건너뜁니다.
 * 하루가 끝나기 전에 숫자가 0 으로 떨어지면 안 되기 때문입니다.
 */
fun challengeStreak(days: List<ChallengeDate>, today: LocalDate = LocalDate.now()): Int {
    val todayString = MissionDates.format(today)
    var streak = 0

    for (day in days.sortedByDescending { it.targetDate }) {
        when {
            day.isCompleted -> streak++
            day.targetDate == todayString -> continue
            else -> break
        }
    }

    return streak
}

/**
 * 연속 달성 시트에 그릴 7칸을 만듭니다. iOS 의 processChallengeList 와 같은 규칙입니다.
 *
 * 처음 달성한 날을 시작점으로 잡고 거기서 7일을 펼칩니다.
 * 서버가 주지 않은 날(아직 오지 않은 날)은 빈 칸으로 둡니다.
 */
fun challengeDays(days: List<ChallengeDate>): List<DailyMission> {
    if (days.isEmpty()) return emptyList()

    val sorted = days.sortedBy { it.targetDate }
    val completedBy = days.associate { it.targetDate to it.isCompleted }

    val firstCompleted = sorted.firstOrNull { it.isCompleted }
        ?.let { MissionDates.parseOrNull(it.targetDate) }

    // 달성한 날이 하루도 없으면 받은 날짜를 그대로 보여 줍니다.
    if (firstCompleted == null) {
        return sorted.mapNotNull { day ->
            MissionDates.parseOrNull(day.targetDate)?.let {
                DailyMission(MissionDates.dayLabel(it), MissionChallengeStatus.FAILED)
            }
        }
    }

    return (0 until CHALLENGE_SLOTS).map { offset ->
        val date = firstCompleted.plusDays(offset.toLong())

        val status = when (completedBy[MissionDates.format(date)]) {
            true -> MissionChallengeStatus.COMPLETED
            false -> MissionChallengeStatus.FAILED
            null -> MissionChallengeStatus.NONE
        }

        DailyMission(MissionDates.dayLabel(date), status)
    }
}
