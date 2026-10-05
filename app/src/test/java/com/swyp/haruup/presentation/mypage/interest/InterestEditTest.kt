package com.swyp.haruup.presentation.mypage.interest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 관심사 경로를 만드는 규칙과 완료 버튼이 켜지는 조건을 고정합니다.
 *
 * 서버는 항상 세 칸짜리 경로를 받습니다. 칸 수가 어긋나면 저장이 조용히 실패하므로
 * 목표가 없는 분류까지 포함해 경로 구성을 확인합니다.
 */
class InterestEditTest {

    private val interest = InterestOption(1, "외국어 공부")
    private val detail = InterestOption(11, "영어")
    private val goal = InterestOption(21, "회화 공부")
    private val directGoal = InterestOption(22, "직접 입력할게요")
    private val otherDetail = InterestOption(19, "기타")

    private fun state(
        selectedInterest: InterestOption? = interest,
        selectedDetail: InterestOption? = detail,
        selectedGoal: InterestOption? = goal,
        goals: List<InterestOption> = listOf(goal, directGoal),
        customDetailName: String? = null,
        customGoalName: String? = null,
        savedPath: List<String> = emptyList(),
        isSubmitting: Boolean = false,
    ) = InterestEditUiState(
        selectedInterest = selectedInterest,
        selectedDetail = selectedDetail,
        selectedGoal = selectedGoal,
        goals = goals,
        customDetailName = customDetailName,
        customGoalName = customGoalName,
        savedPath = savedPath,
        isSubmitting = isSubmitting,
    )

    // MARK: - 직접 입력 판별

    @Test
    fun `기타나 직접이 들어간 항목은 직접 입력으로 본다`() {
        assertTrue(otherDetail.isDirectInput())
        assertTrue(directGoal.isDirectInput())
        assertTrue(InterestOption(1, "직접 입력").isDirectInput())
    }

    @Test
    fun `보통 항목은 직접 입력이 아니다`() {
        assertFalse(interest.isDirectInput())
        assertFalse(goal.isDirectInput())
    }

    // MARK: - 경로 구성

    @Test
    fun `세 단계를 다 고르면 경로가 세 칸이 된다`() {
        assertEquals(listOf("외국어 공부", "영어", "회화 공부"), state().currentPath)
    }

    @Test
    fun `목표가 없는 분류는 마지막 칸을 빈 문자열로 채운다`() {
        // 서버가 세 칸을 기대하므로 빈 칸이라도 자리를 채워야 합니다.
        val path = state(selectedGoal = null, goals = emptyList()).currentPath

        assertEquals(listOf("외국어 공부", "영어", ""), path)
        assertEquals(InterestEditUiState.PATH_SIZE, path.size)
    }

    @Test
    fun `직접 적은 이름이 고른 항목 이름보다 먼저다`() {
        val path = state(
            selectedDetail = otherDetail,
            selectedGoal = directGoal,
            customDetailName = "스페인어",
            customGoalName = "여행 회화",
        ).currentPath

        assertEquals(listOf("외국어 공부", "스페인어", "여행 회화"), path)
    }

    // MARK: - 목표 칸에 보이는 이름

    @Test
    fun `세부 관심사를 고르기 전에는 목표 칸이 비어 있다`() {
        assertNull(state(selectedDetail = null, selectedGoal = null).goalDisplayName)
    }

    @Test
    fun `고를 목표가 없으면 그렇게 알려준다`() {
        val uiState = state(selectedGoal = null, goals = emptyList())

        assertEquals(InterestEditUiState.NO_GOAL, uiState.goalDisplayName)
    }

    @Test
    fun `직접 적은 목표가 있으면 그 이름을 보여준다`() {
        val uiState = state(selectedGoal = directGoal, customGoalName = "여행 회화")

        assertEquals("여행 회화", uiState.goalDisplayName)
    }

    // MARK: - 목표 칸 활성화

    @Test
    fun `세부 관심사를 고르기 전에는 목표 칸이 눌린다`() {
        // 초기 상태에서 회색으로 보이지 않도록 iOS 도 활성화해 둡니다.
        assertTrue(state(selectedDetail = null, selectedGoal = null, goals = emptyList()).isGoalEnabled)
    }

    @Test
    fun `고를 목표가 없으면 목표 칸이 눌리지 않는다`() {
        assertFalse(state(selectedGoal = null, goals = emptyList()).isGoalEnabled)
    }

    // MARK: - 완료 버튼

    @Test
    fun `저장된 경로와 같으면 완료할 수 없다`() {
        val uiState = state(savedPath = listOf("외국어 공부", "영어", "회화 공부"))

        assertFalse(uiState.isModified)
        assertFalse(uiState.isCompleteEnabled)
    }

    @Test
    fun `경로가 달라지면 완료할 수 있다`() {
        val uiState = state(savedPath = listOf("외국어 공부", "영어", "단어 외우기"))

        assertTrue(uiState.isCompleteEnabled)
    }

    @Test
    fun `세부 관심사를 고르지 않으면 완료할 수 없다`() {
        assertFalse(state(selectedDetail = null, selectedGoal = null).isCompleteEnabled)
    }

    @Test
    fun `직접 입력을 골랐는데 적지 않았으면 완료할 수 없다`() {
        val uiState = state(selectedGoal = directGoal, customGoalName = null)

        assertFalse(uiState.isCompleteEnabled)
    }

    @Test
    fun `저장하는 동안에는 다시 누를 수 없다`() {
        val uiState = state(savedPath = listOf("외국어 공부", "영어", "단어 외우기"), isSubmitting = true)

        assertFalse(uiState.isCompleteEnabled)
    }
}
