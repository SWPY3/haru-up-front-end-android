package com.swyp.haruup.presentation.curation.chat

import com.swyp.haruup.data.model.ChatbotAnswerData
import com.swyp.haruup.data.model.ChatbotMission
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurationChatViewModelTest {

    // MARK: - isAffirmative

    @Test
    fun `긍정 표현은 긍정으로 판단한다`() {
        listOf("네", "좋아요", "마무리할게요", "이제 만들어주세요", "충분해요").forEach {
            assertTrue(it, CurationChatViewModel.isAffirmative(it))
        }
    }

    @Test
    fun `부정 표현은 부정으로 판단한다`() {
        listOf("아니요", "아뇨", "나중에요", "계속할래요").forEach {
            assertFalse(it, CurationChatViewModel.isAffirmative(it))
        }
    }

    @Test
    fun `부정어가 섞이면 긍정어가 있어도 부정으로 판단한다`() {
        // "아니요, 미션 만들어주세요" 는 긍정어 "만들" 을 포함하지만 부정이어야 한다.
        assertFalse(CurationChatViewModel.isAffirmative("아니요, 미션 만들어주세요"))
        assertFalse(CurationChatViewModel.isAffirmative("아직 아니에요. 좋은 질문 더 주세요"))
    }

    @Test
    fun `어느 쪽도 아니면 부정으로 둔다`() {
        assertFalse(CurationChatViewModel.isAffirmative("음..."))
        assertFalse(CurationChatViewModel.isAffirmative(""))
    }

    // MARK: - 응답 종류 판별

    @Test
    fun `완료가 다른 조건보다 우선한다`() {
        val data = ChatbotAnswerData(
            isCompleted = true,
            isValidGoal = false,
            awaitingFinishConfirmation = true,
            question = "남은 질문",
            missions = listOf(
                ChatbotMission(id = 1, missionContent = "미션", difficulty = 1, expEarned = 100)
            ),
        )
        assertEquals(ChatbotAnswerData.Kind.COMPLETED, data.kind)
    }

    @Test
    fun `목표 검증 실패가 마무리 확인보다 우선한다`() {
        val data = ChatbotAnswerData(
            isValidGoal = false,
            awaitingFinishConfirmation = true,
            question = "남은 질문",
        )
        assertEquals(ChatbotAnswerData.Kind.GOAL_REJECTED, data.kind)
    }

    @Test
    fun `마무리 확인이 다음 질문보다 우선한다`() {
        val data = ChatbotAnswerData(awaitingFinishConfirmation = true, question = "이대로 마무리할까요?")
        assertEquals(ChatbotAnswerData.Kind.FINISH_CONFIRM, data.kind)
    }

    @Test
    fun `질문만 있으면 다음 질문이다`() {
        val data = ChatbotAnswerData(question = "하루에 얼마나 투자할 수 있나요?")
        assertEquals(ChatbotAnswerData.Kind.NEXT_QUESTION, data.kind)
    }

    @Test
    fun `아무 조건에도 걸리지 않으면 알 수 없는 응답이다`() {
        assertEquals(ChatbotAnswerData.Kind.UNKNOWN, ChatbotAnswerData().kind)
    }
}
