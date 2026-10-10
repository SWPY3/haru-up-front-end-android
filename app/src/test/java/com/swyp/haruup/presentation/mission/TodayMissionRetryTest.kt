package com.swyp.haruup.presentation.mission

import com.swyp.haruup.data.model.RetryMissionData
import com.swyp.haruup.data.model.RetryMissionGroup
import com.swyp.haruup.data.model.RetryMissionItem
import com.swyp.haruup.util.FakeMissionService
import com.swyp.haruup.util.FakeTokenStorage
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * 재추천을 눌렀을 때 고른 미션이 지켜지는지, 횟수가 맞게 보이는지 확인합니다.
 *
 * 고르던 미션이 재추천으로 사라지면 사용자는 처음부터 다시 봐야 합니다.
 * 그게 이 화면에서 가장 중요한 약속이라 테스트로 고정합니다.
 */
class TodayMissionRetryTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private fun mission(id: Int) = MissionItem(
        id = id,
        content = "미션 $id",
        difficulty = MissionDifficulty.LOW,
        expEarned = 100,
    )

    private fun freshMissions(vararg ids: Int, retryCount: Int = 1) = RetryMissionData(
        missions = listOf(
            RetryMissionGroup(
                memberInterestId = TodayMissionViewModel.CHATBOT_INTEREST_ID,
                data = ids.map { RetryMissionItem(memberMissionId = it, content = "새 미션 $it") },
            )
        ),
        retryCount = retryCount,
    )

    private fun viewModel(service: FakeMissionService) =
        TodayMissionViewModel(service, FakeTokenStorage()).apply {
            setMissions(listOf(mission(1), mission(2), mission(3)))
        }

    @Test
    fun `고른 미션은 남고 나머지만 새 미션으로 바뀐다`() = runTest {
        val service = FakeMissionService(retry = freshMissions(11, 12, 13))
        val vm = viewModel(service)

        vm.onMissionClick(1)
        vm.onRetryClick()
        advanceUntilIdle()

        val ids = vm.uiState.value.missions.map { it.id }

        assertEquals(1, ids.first())
        // 세 자리 중 하나는 고른 미션이 차지하므로 새 미션은 둘만 채웁니다.
        assertEquals(listOf(1, 11, 12), ids)
        assertTrue(vm.uiState.value.isSelected(1))
    }

    @Test
    fun `아무것도 고르지 않았으면 전부 새 미션으로 바뀐다`() = runTest {
        val service = FakeMissionService(retry = freshMissions(11, 12, 13))
        val vm = viewModel(service)

        vm.onRetryClick()
        advanceUntilIdle()

        assertEquals(listOf(11, 12, 13), vm.uiState.value.missions.map { it.id })
    }

    @Test
    fun `챗봇 미션은 제외 목록을 보내지 않는다`() = runTest {
        val service = FakeMissionService(retry = freshMissions(11))
        val vm = viewModel(service)

        vm.onMissionClick(1)
        vm.onRetryClick()
        advanceUntilIdle()

        val request = service.retryRequest!!
        assertEquals(TodayMissionViewModel.CHATBOT_INTEREST_ID, request.memberInterestId)
        assertNull(request.excludeMemberMissionIds)
    }

    @Test
    fun `서버가 알려준 횟수를 그대로 보여준다`() = runTest {
        val service = FakeMissionService(retry = freshMissions(11, retryCount = 3))
        val vm = viewModel(service)

        vm.onRetryClick()
        advanceUntilIdle()

        assertEquals(3, vm.uiState.value.retryCount)
        assertEquals("다른 추천 3/5회", vm.uiState.value.retryLabel)
    }

    @Test
    fun `횟수를 다 쓰면 더 누를 수 없다`() = runTest {
        val service = FakeMissionService(
            retry = freshMissions(11, retryCount = TodayMissionUiState.MAX_RETRY)
        )
        val vm = viewModel(service)

        vm.onRetryClick()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isRetryEnabled)

        // 더 눌러도 요청이 나가지 않아야 합니다.
        service.retryRequest = null
        vm.onRetryClick()
        advanceUntilIdle()

        assertNull(service.retryRequest)
    }

    @Test
    fun `실패하면 목록을 건드리지 않는다`() = runTest {
        // 횟수만 쓰고 빈 화면이 되는 쪽이 더 나쁩니다.
        val service = FakeMissionService(retry = freshMissions(11), shouldFail = true)
        val vm = viewModel(service)

        vm.onRetryClick()
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 3), vm.uiState.value.missions.map { it.id })
        assertEquals(0, vm.uiState.value.retryCount)
        assertFalse(vm.uiState.value.isLoading)
    }
}
