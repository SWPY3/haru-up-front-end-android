package com.swyp.haruup.presentation.home

import com.swyp.haruup.data.model.MissionListItem
import com.swyp.haruup.data.model.MissionStatus
import com.swyp.haruup.util.FakeMissionService
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
 * 완료와 삭제가 서버 응답에 따라 화면을 바꾸는지 확인합니다.
 *
 * 서버가 실패했는데도 화면만 바꾸면, 다시 들어왔을 때 되돌아가 있어
 * 사용자는 완료가 취소된 것처럼 느낍니다. 그 경로를 고정합니다.
 */
class HomeMissionApiTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val mission = MissionListItem(
        id = 1,
        missionStatus = MissionStatus.ACTIVE,
        missionContent = "영어 단어 외우기",
        difficulty = 1,
        expEarned = 100,
    )

    private fun service(vararg items: MissionListItem, fail: Boolean = false) =
        FakeMissionService(missions = items.toList(), shouldFail = fail)

    @Test
    fun `오늘의 미션은 완료한 것까지 함께 받아온다`() = runTest {
        val completed = mission.copy(id = 2, missionStatus = MissionStatus.COMPLETED)
        val service = service(mission, completed)
        val vm = HomeViewModel(service)
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.todayMissions.size)
        // 완료한 미션은 완료 표시가 되어 있어야 합니다.
        assertTrue(vm.uiState.value.isCompleted(2))
        assertFalse(vm.uiState.value.isCompleted(1))

        assertEquals("COMPLETED,ACTIVE", service.requestedMissionQueries.first().first)
    }

    @Test
    fun `완료하면 서버에 COMPLETED 로 알린다`() = runTest {
        val service = service(mission)
        val vm = HomeViewModel(service)
        advanceUntilIdle()

        vm.onMissionSettingClick(vm.uiState.value.todayMissions.first())
        vm.onCompleteClick()
        advanceUntilIdle()

        val sent = service.statusUpdates.single().missions.single()
        assertEquals(1, sent.memberMissionId)
        assertEquals(MissionStatus.COMPLETED, sent.missionStatus)

        assertTrue(vm.uiState.value.isCompleted(1))
        assertEquals(100, vm.uiState.value.completedExp)
    }

    @Test
    fun `완료가 실패하면 화면을 바꾸지 않는다`() = runTest {
        val service = service(mission)
        val vm = HomeViewModel(service)
        advanceUntilIdle()

        // 목록은 받아 둔 뒤부터 실패하게 만듭니다.
        service.shouldFail = true

        vm.onMissionSettingClick(vm.uiState.value.todayMissions.first())
        vm.onCompleteClick()
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isCompleted(1))
        assertNull(vm.uiState.value.completedExp)
    }

    @Test
    fun `삭제하면 서버에 INACTIVE 로 알리고 목록에서 뺀다`() = runTest {
        val service = service(mission)
        val vm = HomeViewModel(service)
        advanceUntilIdle()

        vm.onMissionSettingClick(vm.uiState.value.todayMissions.first())
        vm.onDeleteClick()
        vm.onDeleteConfirm()
        advanceUntilIdle()

        assertEquals(MissionStatus.INACTIVE, service.statusUpdates.single().missions.single().missionStatus)
        assertTrue(vm.uiState.value.todayMissions.isEmpty())
    }

    @Test
    fun `삭제가 실패하면 목록에 그대로 남는다`() = runTest {
        val service = service(mission)
        val vm = HomeViewModel(service)
        advanceUntilIdle()

        service.shouldFail = true

        vm.onMissionSettingClick(vm.uiState.value.todayMissions.first())
        vm.onDeleteClick()
        vm.onDeleteConfirm()
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.todayMissions.size)
    }
}
