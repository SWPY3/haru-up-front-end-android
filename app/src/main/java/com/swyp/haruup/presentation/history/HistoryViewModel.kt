package com.swyp.haruup.presentation.history

import androidx.lifecycle.ViewModel
import com.swyp.haruup.presentation.mission.MissionItem
import dagger.hilt.android.lifecycle.HiltViewModel
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
class HistoryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun onPreviousMonthClick() {
        _uiState.update { it.copy(yearMonth = it.yearMonth.minusMonths(1)) }
    }

    fun onNextMonthClick() {
        _uiState.update { state ->
            if (state.canGoNext) state.copy(yearMonth = state.yearMonth.plusMonths(1)) else state
        }
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
    }

    /** TODO: 월별 기록 조회 API 로 교체 */
    fun setSummary(summary: MonthlyMissionSummary) {
        _uiState.update { it.copy(summary = summary) }
    }

    /** TODO: 날짜별 미션 조회 API 로 교체 */
    fun setMissionsByDate(missionsByDate: Map<LocalDate, List<MissionItem>>) {
        _uiState.update { it.copy(missionsByDate = missionsByDate) }
    }
}
