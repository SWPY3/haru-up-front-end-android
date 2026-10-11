package com.swyp.haruup.presentation.chart

import com.swyp.haruup.data.model.JobDetail
import com.swyp.haruup.util.FakeChartService
import com.swyp.haruup.util.FakeJobService
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** 차트 화면이 세부 직무를 서버에서 받아 필터에 쓰는지 확인합니다. */
class ChartJobDetailTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val jobService = FakeJobService(
        detailsByJob = mapOf(
            1 to listOf(JobDetail(id = 2, jobId = 1, jobDetailName = "IT/개발자")),
            3 to listOf(JobDetail(id = 19, jobId = 3, jobDetailName = "대학생")),
        ),
    )

    @Test
    fun `모든 직업의 세부 직무를 받아 섹션으로 보여 준다`() = runTest {
        val vm = ChartViewModel(FakeChartService(), jobService)
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 3, 4), jobService.requestedJobIds.sorted())
        val section = vm.uiState.value.filterSections.first { it.title == "세부 직무" }
        assertEquals(listOf("IT/개발자", "대학생"), section.tags)
    }

    @Test
    fun `고른 세부 직무는 서버 id 로 조회한다`() = runTest {
        val chartService = FakeChartService()
        val vm = ChartViewModel(chartService, jobService)
        advanceUntilIdle()

        vm.onFilterApplied(listOf("대학생"))
        advanceUntilIdle()

        assertEquals(listOf(19), chartService.lastJobDetailIds)
    }

    @Test
    fun `하나라도 못 받으면 세부 직무 섹션을 감춘다`() = runTest {
        jobService.failingJobId = 2
        val vm = ChartViewModel(FakeChartService(), jobService)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.jobDetails.isEmpty())
        assertFalse(vm.uiState.value.filterSections.any { it.title == "세부 직무" })
    }
}
