package com.swyp.haruup.presentation.home

import com.swyp.haruup.data.model.HomeMemberInfoData
import com.swyp.haruup.util.FakeMemberService
import com.swyp.haruup.util.FakeMissionService
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * 홈 상단 회원 정보가 응답대로 들어오는지, 못 받아왔을 때 화면이 비어 버리지 않는지 확인합니다.
 */
class HomeMemberInfoTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val member = HomeMemberInfoData(
        characterId = 2,
        levelNumber = 3,
        nickname = "하루나루",
        currentExp = 300,
        maxExp = 1000,
        interests = listOf(listOf("외국어 공부", "영어", "회화 공부")),
    )

    private fun viewModel(memberService: FakeMemberService) =
        HomeViewModel(FakeMissionService(), memberService)

    @Test
    fun `회원 정보를 화면 모델로 옮긴다`() = runTest {
        val vm = viewModel(FakeMemberService(members = listOf(member)))
        advanceUntilIdle()

        val info = vm.uiState.value.memberInfo

        assertEquals(2, info.characterId)
        assertEquals(3, info.level)
        assertEquals("하루나루", info.nickname)
        assertEquals(300, info.currentExp)
        assertEquals(1000, info.maxExp)
    }

    @Test
    fun `관심사는 첫 번째의 가장 큰 분류만 쓴다`() = runTest {
        val vm = viewModel(FakeMemberService(members = listOf(member)))
        advanceUntilIdle()

        assertEquals("외국어 공부", vm.uiState.value.memberInfo.interest)
    }

    @Test
    fun `관심사가 없어도 비어 있을 뿐 깨지지 않는다`() = runTest {
        val vm = viewModel(FakeMemberService(members = listOf(member.copy(interests = emptyList()))))
        advanceUntilIdle()

        assertEquals("", vm.uiState.value.memberInfo.interest)
        // 말풍선은 비어 있으면 기본 문구로 채웁니다.
        assertTrue(vm.uiState.value.bubbleMessages[1].contains("외국어 공부"))
    }

    @Test
    fun `경험치 진행률을 계산한다`() = runTest {
        val vm = viewModel(FakeMemberService(members = listOf(member)))
        advanceUntilIdle()

        assertEquals(0.3f, vm.uiState.value.memberInfo.expProgress, 0.001f)
    }

    @Test
    fun `아직 경험치가 없으면 0 으로 둔다`() = runTest {
        // maxExp 가 0 일 때 나누면 터지므로 0 으로 떨어뜨립니다.
        val vm = viewModel(FakeMemberService(members = listOf(member.copy(currentExp = 0, maxExp = 0))))
        advanceUntilIdle()

        assertEquals(0f, vm.uiState.value.memberInfo.expProgress, 0.001f)
    }

    @Test
    fun `못 받아오면 보여 주던 값을 그대로 둔다`() = runTest {
        val service = FakeMemberService(members = listOf(member))
        val vm = viewModel(service)
        advanceUntilIdle()

        service.shouldFail = true
        vm.loadMemberInfo()
        advanceUntilIdle()

        // 통신이 한 번 실패했다고 레벨과 닉네임이 사라지면 안 됩니다.
        assertEquals("하루나루", vm.uiState.value.memberInfo.nickname)
        assertEquals(3, vm.uiState.value.memberInfo.level)
    }

    @Test
    fun `응답이 비어 있어도 값을 덮어쓰지 않는다`() = runTest {
        val service = FakeMemberService(members = listOf(member))
        val vm = viewModel(service)
        advanceUntilIdle()

        service.members = emptyList()
        vm.loadMemberInfo()
        advanceUntilIdle()

        assertEquals("하루나루", vm.uiState.value.memberInfo.nickname)
    }
}
