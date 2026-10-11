package com.swyp.haruup.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.core.util.MissionDates
import com.swyp.haruup.data.model.MissionStatus
import com.swyp.haruup.network.service.MissionService
import com.swyp.haruup.presentation.mission.MissionItem
import com.swyp.haruup.presentation.mission.toMissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class HistoryUiState(
    val yearMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val summary: MonthlyMissionSummary = MonthlyMissionSummary(),
    /** 날짜별로 그날 완료한 미션 목록 */
    val missionsByDate: Map<LocalDate, List<MissionItem>> = emptyMap(),
    /** 성장 차트에 쓰는 최근 5개월 달성일 */
    val growthPoints: List<GrowthPoint> = emptyList(),
    val isLoading: Boolean = false,
) {
    /** 선택한 날짜에 완료한 미션 */
    val selectedMissions: List<MissionItem> = missionsByDate[selectedDate].orEmpty()

    val selectedDateLabel: String =
        "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일 완료한 미션"

    val days: List<CalendarDay> = buildCalendarGrid(yearMonth)

    val monthLabel: String = "${yearMonth.year}년 ${yearMonth.monthValue}월"

    /** 다음 달로는 이번 달을 넘어 이동할 수 없습니다. */
    val canGoNext: Boolean = yearMonth < YearMonth.now()
}

/**
 * 기록 탭. iOS 의 HistoryViewModel 에 대응합니다.
 *
 * 이번 단계에서는 캘린더와 월별 통계만 다룹니다.
 * 선택한 날짜의 미션 목록과 성장 차트는 이어서 붙입니다.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val missionService: MissionService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    /** 이미 받아 온 달은 다시 받지 않습니다. 달을 앞뒤로 넘길 때마다 조회하면 낭비이기 때문입니다. */
    private val loadedMonths = mutableSetOf<YearMonth>()

    init {
        loadMonth(_uiState.value.yearMonth)
        loadSelectedDate(_uiState.value.selectedDate)
        loadGrowth()
    }

    /** 그 달의 일별 완료 개수를 받아 캘린더 도장에 씁니다. */
    private fun loadMonth(yearMonth: YearMonth) {
        if (!loadedMonths.add(yearMonth)) return

        viewModelScope.launch {
            val data = runCatching { missionService.monthlyMissions(MissionDates.format(yearMonth)) }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data

            if (data == null) {
                // 실패한 달은 다시 시도할 수 있게 기록에서 뺍니다.
                loadedMonths.remove(yearMonth)
                return@launch
            }

            val counts = data.missionCounts.mapNotNull { count ->
                MissionDates.parseOrNull(count.targetDate)
                    ?.let { DailyMissionCount(it, count.completedCount) }
            }

            _uiState.update { state ->
                // 달을 넘기는 사이에 응답이 와도 지금 보고 있는 달만 반영합니다.
                if (state.yearMonth != yearMonth) return@update state

                state.copy(
                    summary = MonthlyMissionSummary(
                        dailyCounts = counts,
                        totalMissionCount = data.totalMissionCount,
                        totalCompletedDays = data.totalCompletedDays,
                    )
                )
            }
        }
    }

    /** 고른 날짜에 완료한 미션 목록입니다. */
    private fun loadSelectedDate(date: LocalDate) {
        if (_uiState.value.missionsByDate.containsKey(date)) return

        viewModelScope.launch {
            val missions = runCatching {
                missionService.missions(
                    missionStatus = MissionStatus.COMPLETED,
                    targetDate = MissionDates.format(date),
                )
            }.getOrNull()
                ?.takeIf { it.success }
                ?.data
                ?: return@launch

            _uiState.update {
                it.copy(missionsByDate = it.missionsByDate + (date to missions.map { m -> m.toMissionItem() }))
            }
        }
    }

    /** 최근 6개월의 달성일수입니다. */
    private fun loadGrowth() {
        viewModelScope.launch {
            val (startMonth, endMonth) = MissionDates.growthRange()

            val data = runCatching { missionService.growth(startMonth, endMonth) }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data
                ?: return@launch

            val points = data.monthlyData.map { month ->
                // "2026-09" 의 뒤 두 자리만 써서 "9월" 로 보여 줍니다.
                val label = month.targetMonth.substringAfter('-').trimStart('0')
                GrowthPoint("${label}월", month.completedDays)
            }

            _uiState.update { it.copy(growthPoints = points) }
        }
    }

    fun onPreviousMonthClick() {
        _uiState.update { it.copy(yearMonth = it.yearMonth.minusMonths(1)) }
        loadMonth(_uiState.value.yearMonth)
    }

    fun onNextMonthClick() {
        _uiState.update { state ->
            if (state.canGoNext) state.copy(yearMonth = state.yearMonth.plusMonths(1)) else state
        }
        loadMonth(_uiState.value.yearMonth)
    }

    /**
     * 날짜를 고릅니다.
     * 앞뒤 달 날짜를 누르면 그 달로 함께 이동합니다. (iOS 와 동일)
     */
    fun onDayClick(day: CalendarDay) {
        _uiState.update {
            it.copy(
                selectedDate = day.date,
                yearMonth = YearMonth.from(day.date),
            )
        }

        loadMonth(_uiState.value.yearMonth)
        loadSelectedDate(day.date)
    }

}
