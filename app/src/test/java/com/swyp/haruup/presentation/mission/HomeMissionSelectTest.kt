package com.swyp.haruup.presentation.mission

import com.swyp.haruup.data.model.RetryMissionData
import com.swyp.haruup.data.model.RetryMissionGroup
import com.swyp.haruup.data.model.RetryMissionItem
import com.swyp.haruup.util.FakeInterestService
import com.swyp.haruup.util.FakeMissionService
import com.swyp.haruup.util.FakeTokenStorage
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

/**
 * 홈에서 미션을 고르는 흐름을 확인합니다.
 *
 * 소개 화면이 하루 한 번만 뜨는지, 이미 고른 미션이 다시 나오지 않는지,
 * 확정이 실패했을 때 화면이 닫히지 않는지를 고정합니다.
 */
class HomeMissionSelectTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 10, 11)

    private fun group(interestId: Int, vararg ids: Int) = RetryMissionGroup(
        memberInterestId = interestId,
        data = ids.map { RetryMissionItem(memberMissionId = it, content = "미션 $it") },
    )

    private fun viewModel(
        service: FakeMissionService = FakeMissionService(),
        interests: FakeInterestService = FakeInterestService(),
        storage: FakeTokenStorage = FakeTokenStorage(),
    ) = TodayMissionViewModel(service, interests, storage)

    // MARK: - 소개 화면

    @Test
    fun `처음 고르러 온 날은 소개 화면을 보여 준다`() = runTest {
        val vm = viewModel()

        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isIntroVisible)
        assertTrue(vm.uiState.value.isClosable)
    }

    @Test
    fun `같은 날 다시 오면 소개 화면 없이 목록을 보여 준다`() = runTest {
        val storage = FakeTokenStorage(lastMissionIntroDate = "2026-10-11")
        val vm = viewModel(storage = storage)

        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isIntroVisible)
    }

    @Test
    fun `소개 화면을 보여 주면 오늘 날짜를 기록한다`() = runTest {
        val storage = FakeTokenStorage()
        viewModel(storage = storage).startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertEquals("2026-10-11", storage.getLastMissionIntroDate())
    }

    @Test
    fun `미션을 더하러 왔으면 소개 화면을 건너뛴다`() = runTest {
        val vm = viewModel()

        vm.startFromHome(listOf(1), today)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isIntroVisible)
    }

    // MARK: - 추천

    @Test
    fun `관심사가 있으면 그 관심사로 추천받는다`() = runTest {
        val service = FakeMissionService(
            recommend = RetryMissionData(missions = listOf(group(7, 1, 2), group(8, 3)), retryCount = 2),
        )
        val vm = viewModel(service, FakeInterestService(memberInterestIds = listOf(7, 8)))

        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertEquals(listOf(7, 8), service.recommendRequest?.memberInterestIds)
        assertEquals(listOf(1, 2, 3), vm.uiState.value.missions.map { it.id })
        assertEquals(2, vm.uiState.value.retryCount)
    }

    @Test
    fun `관심사가 없으면 챗봇 미션을 추천받는다`() = runTest {
        val service = FakeMissionService(recommend = RetryMissionData(missions = listOf(group(0, 1))))
        val vm = viewModel(service, FakeInterestService(memberInterestIds = emptyList()))

        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertEquals(listOf(MissionSource.CHATBOT_INTEREST_ID), service.recommendRequest?.memberInterestIds)
        assertEquals(listOf(1), vm.uiState.value.missions.map { it.id })
    }

    @Test
    fun `오늘 이미 고른 미션은 추천 목록에서 뺀다`() = runTest {
        val service = FakeMissionService(recommend = RetryMissionData(missions = listOf(group(7, 1, 2, 3))))
        val vm = viewModel(service, FakeInterestService(memberInterestIds = listOf(7)))

        vm.startFromHome(listOf(2), today)
        advanceUntilIdle()

        assertEquals(listOf(1, 3), vm.uiState.value.missions.map { it.id })
    }

    @Test
    fun `추천을 못 받으면 다시 불러오기를 보여 준다`() = runTest {
        val vm = viewModel(FakeMissionService(shouldFail = true), FakeInterestService(listOf(7)))

        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isLoadFailed)
        assertNotNull(vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test
    fun `관심사를 못 받아도 다시 불러오기를 보여 준다`() = runTest {
        val vm = viewModel(interests = FakeInterestService(shouldFail = true))

        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isLoadFailed)
    }

    // MARK: - 선택 한도

    @Test
    fun `오늘 고른 미션까지 합쳐 5개를 넘지 않는다`() = runTest {
        val service = FakeMissionService(recommend = RetryMissionData(missions = listOf(group(7, 11, 12, 13))))
        val vm = viewModel(service, FakeInterestService(listOf(7)))

        vm.startFromHome(listOf(1, 2, 3), today)
        advanceUntilIdle()

        vm.onMissionClick(11)
        vm.onMissionClick(12)
        vm.onMissionClick(13)

        assertEquals(setOf(11, 12), vm.uiState.value.selectedIds)
        assertEquals(0, vm.uiState.value.remainingCount)
        assertTrue(vm.uiState.value.isDisabled(13))
    }

    // MARK: - 재추천

    @Test
    fun `관심사 미션을 재추천할 때는 고른 미션을 빼 달라고 한다`() = runTest {
        val service = FakeMissionService(
            recommend = RetryMissionData(missions = listOf(group(7, 1, 2, 3))),
            retry = RetryMissionData(missions = listOf(group(7, 21, 22, 23)), retryCount = 1),
        )
        val vm = viewModel(service, FakeInterestService(listOf(7)))
        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        vm.onMissionClick(1)
        vm.onRetryClick()
        advanceUntilIdle()

        assertEquals(7, service.retryRequest?.memberInterestId)
        assertEquals(listOf(1), service.retryRequest?.excludeMemberMissionIds)
        assertEquals(listOf(1, 21, 22), vm.uiState.value.missions.map { it.id })
    }

    // MARK: - 확정

    @Test
    fun `확정에 성공하면 고른 미션만 보내고 닫는다`() = runTest {
        val service = FakeMissionService(recommend = RetryMissionData(missions = listOf(group(7, 11, 12))))
        val storage = FakeTokenStorage()
        val vm = viewModel(service, FakeInterestService(listOf(7)), storage)
        vm.startFromHome(listOf(1), today)
        advanceUntilIdle()

        vm.onMissionClick(11)
        var done = false
        vm.onCompleteClick { done = true }
        advanceUntilIdle()

        assertTrue(done)
        // 오늘 이미 고른 미션은 다시 보내지 않습니다.
        assertEquals(listOf(11), service.selectedRequest?.memberMissionIds)
        // 홈에서 고른 것은 온보딩과 상관없습니다.
        assertFalse(storage.isOnboardingCompleted())
    }

    @Test
    fun `확정에 실패하면 닫지 않고 알린다`() = runTest {
        val service = FakeMissionService(recommend = RetryMissionData(missions = listOf(group(7, 11))))
        val vm = viewModel(service, FakeInterestService(listOf(7)))
        vm.startFromHome(emptyList(), today)
        advanceUntilIdle()

        vm.onMissionClick(11)
        service.shouldFail = true
        var done = false
        vm.onCompleteClick { done = true }
        advanceUntilIdle()

        assertFalse(done)
        assertEquals(TodayMissionViewModel.SELECT_ERROR_MESSAGE, vm.uiState.value.errorMessage)

        vm.onErrorShown()
        assertNull(vm.uiState.value.errorMessage)
    }
}
