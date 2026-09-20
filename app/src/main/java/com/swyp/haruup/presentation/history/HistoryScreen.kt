package com.swyp.haruup.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mission.MissionDifficulty
import com.swyp.haruup.presentation.mission.MissionItem
import com.swyp.haruup.presentation.history.component.CalendarCard
import com.swyp.haruup.presentation.history.component.DailyMissionCard
import java.time.LocalDate
import java.time.YearMonth

private val H_MARGIN = 20.dp
private val TITLE_TOP = 16.dp
private val SECTION_SPACING = 16.dp

/**
 * 메인 탭의 기록. iOS 의 HistoryViewController 에 대응합니다.
 *
 * 이번 단계에서는 캘린더와 월별 통계만 구성했습니다.
 * 선택한 날짜의 미션 목록과 성장 차트는 이어서 붙입니다.
 */
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryContent(
        uiState = uiState,
        onPreviousClick = viewModel::onPreviousMonthClick,
        onNextClick = viewModel::onNextMonthClick,
        onDayClick = viewModel::onDayClick,
        modifier = modifier,
    )
}

@Composable
private fun HistoryContent(
    uiState: HistoryUiState,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onDayClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = H_MARGIN)
            .padding(PaddingValues(top = TITLE_TOP, bottom = SECTION_SPACING)),
        verticalArrangement = Arrangement.spacedBy(SECTION_SPACING),
    ) {
        Text(text = "나의 기록", style = HaruUpType.title3, color = HaruUpColor.AppBlack)

        CalendarCard(
            yearMonth = uiState.yearMonth,
            monthLabel = uiState.monthLabel,
            days = uiState.days,
            selectedDate = uiState.selectedDate,
            summary = uiState.summary,
            canGoNext = uiState.canGoNext,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            onDayClick = onDayClick,
        )

        DailyMissionCard(
            title = uiState.selectedDateLabel,
            missions = uiState.selectedMissions,
        )

        // TODO: 성장 차트 추가
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun HistoryPreview() {
    val month = YearMonth.of(2026, 9)

    HaruUpTheme {
        HistoryContent(
            uiState = HistoryUiState(
                yearMonth = month,
                selectedDate = LocalDate.of(2026, 9, 12),
                summary = MonthlyMissionSummary(
                    dailyCounts = listOf(
                        DailyMissionCount(LocalDate.of(2026, 9, 1), 1),
                        DailyMissionCount(LocalDate.of(2026, 9, 2), 3),
                        DailyMissionCount(LocalDate.of(2026, 9, 5), 5),
                        DailyMissionCount(LocalDate.of(2026, 9, 12), 2),
                    ),
                    totalMissionCount = 11,
                    totalCompletedDays = 4,
                ),
                missionsByDate = mapOf(
                    LocalDate.of(2026, 9, 12) to listOf(
                        MissionItem(1, "영어 회화 표현 5개 외우기", null, MissionDifficulty.MEDIUM, 100),
                        MissionItem(2, "영어 뉴스 기사 하나 읽기", null, MissionDifficulty.HIGH, 200),
                    ),
                ),
            ),
            onPreviousClick = {}, onNextClick = {}, onDayClick = {},
        )
    }
}
