package com.swyp.haruup.presentation.curation

import com.swyp.haruup.data.model.ChatbotMission
import com.swyp.haruup.data.model.ChatbotSetupRequest
import com.swyp.haruup.util.FakeChatbotService
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * 챗봇을 마쳤을 때 캐릭터와 닉네임이 서버에 저장되는지 확인합니다.
 *
 * 저장이 빠지면 홈과 마이페이지에 닉네임·캐릭터가 비어 보입니다.
 * 반대로 저장 실패로 화면이 막히면 챗봇 대화를 처음부터 다시 해야 합니다. 두 경우를 고정합니다.
 */
class CurationViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val missions = listOf(
        ChatbotMission(id = 1, missionContent = "미션", difficulty = 1, expEarned = 100),
    )

    @Test
    fun `고른 캐릭터와 닉네임을 저장한 뒤 넘어간다`() = runTest {
        val service = FakeChatbotService()
        val vm = CurationViewModel(service).apply { setCharacterId(2) }

        var done = false
        vm.completeChat("하루", missions) { done = true }
        advanceUntilIdle()

        assertEquals(listOf(ChatbotSetupRequest(characterId = 2, nickname = "하루")), service.setupRequests)
        assertTrue(done)
        assertEquals("하루", vm.curationData.value.nickname)
        assertEquals(missions, vm.curationData.value.chatbotMissions)
    }

    @Test
    fun `저장에 실패해도 미션 선택으로 넘어간다`() = runTest {
        val vm = CurationViewModel(FakeChatbotService(shouldFail = true)).apply { setCharacterId(1) }

        var done = false
        vm.completeChat("하루", missions) { done = true }
        advanceUntilIdle()

        assertTrue(done)
    }

    @Test
    fun `캐릭터를 고르지 않았으면 저장하지 않고 넘어간다`() = runTest {
        val service = FakeChatbotService()
        val vm = CurationViewModel(service)

        var done = false
        vm.completeChat("하루", missions) { done = true }
        advanceUntilIdle()

        assertTrue(service.setupRequests.isEmpty())
        assertTrue(done)
    }

    @Test
    fun `저장하는 동안 다시 불러도 한 번만 보낸다`() = runTest {
        val service = FakeChatbotService()
        val vm = CurationViewModel(service).apply { setCharacterId(1) }

        var doneCount = 0
        vm.completeChat("하루", missions) { doneCount++ }
        vm.completeChat("하루", missions) { doneCount++ }
        advanceUntilIdle()

        assertEquals(1, service.setupRequests.size)
        assertEquals(1, doneCount)
    }
}
