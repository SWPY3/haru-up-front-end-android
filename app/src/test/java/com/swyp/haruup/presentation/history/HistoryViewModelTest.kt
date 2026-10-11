package com.swyp.haruup.presentation.history

import com.swyp.haruup.util.FakeMissionService
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.YearMonth

/**
 * 달을 넘길 때 같은 달을 거듭 받아오지 않는지 확인합니다.
 *
 * 에뮬레이터에서는 토큰이 없어 조회가 늘 실패해 이 경로를 확인할 수 없습니다.
 * (실패한 달은 일부러 다시 시도하므로 성공했을 때의 동작이 가려집니다)
 */
class HistoryViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private fun viewModel(service: FakeMissionService) = HistoryViewModel(service)

    @Test
    fun `이미 받아 온 달은 다시 받지 않는다`() = runTest {
        val service = FakeMissionService()
        val vm = viewModel(service)
        advanceUntilIdle()

        val thisMonth = YearMonth.now()

        vm.onPreviousMonthClick()
        advanceUntilIdle()
        vm.onNextMonthClick()
        advanceUntilIdle()

        // 이번 달은 처음 한 번만 받아야 합니다.
        assertEquals(1, service.requestedMonths.count { it == thisMonth.toString() })
    }

    @Test
    fun `새로운 달로 넘어가면 그 달을 받아온다`() = runTest {
        val service = FakeMissionService()
        val vm = viewModel(service)
        advanceUntilIdle()

        vm.onPreviousMonthClick()
        advanceUntilIdle()

        val previous = YearMonth.now().minusMonths(1).toString()
        assertTrue(service.requestedMonths.contains(previous))
    }

    @Test
    fun `조회에 실패한 달은 다시 시도한다`() = runTest {
        // 통신이 한 번 실패했다고 그 달을 영영 비워 두면 안 됩니다.
        val service = FakeMissionService(shouldFail = true)
        val vm = viewModel(service)
        advanceUntilIdle()

        val thisMonth = YearMonth.now()

        vm.onPreviousMonthClick()
        advanceUntilIdle()
        vm.onNextMonthClick()
        advanceUntilIdle()

        assertEquals(2, service.requestedMonths.count { it == thisMonth.toString() })
    }

    @Test
    fun `선택한 날짜는 완료한 미션만 조회한다`() = runTest {
        val service = FakeMissionService()
        viewModel(service)
        advanceUntilIdle()

        val statuses = service.requestedMissionQueries.map { it.first }

        assertTrue(statuses.isNotEmpty())
        assertTrue(statuses.all { it == "COMPLETED" })
    }
}
