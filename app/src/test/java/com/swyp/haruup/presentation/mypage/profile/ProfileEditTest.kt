package com.swyp.haruup.presentation.mypage.profile

import com.swyp.haruup.core.util.NicknameValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 완료 버튼이 켜지는 조건과 경고 문구를 고정합니다.
 *
 * 문구는 큐레이션 쪽과 달라야 합니다. 같은 검사 규칙을 쓰지만 화면마다 다르게 보여 주기 때문입니다.
 */
class ProfileEditTest {

    // MARK: - 완료 버튼

    @Test
    fun `닉네임이 그대로면 완료할 수 없다`() {
        val state = ProfileEditUiState(nickname = "하루나루", savedNickname = "하루나루")

        assertFalse(state.isCompleteEnabled)
    }

    @Test
    fun `닉네임을 고치면 완료할 수 있다`() {
        val state = ProfileEditUiState(nickname = "나루하루", savedNickname = "하루나루")

        assertTrue(state.isCompleteEnabled)
    }

    @Test
    fun `앞뒤 공백만 붙인 것은 고친 것으로 보지 않는다`() {
        val state = ProfileEditUiState(nickname = "  하루나루  ", savedNickname = "하루나루")

        assertFalse(state.isCompleteEnabled)
    }

    @Test
    fun `저장하는 동안에는 다시 누를 수 없다`() {
        val state = ProfileEditUiState(
            nickname = "나루하루",
            savedNickname = "하루나루",
            isSubmitting = true,
        )

        assertFalse(state.isCompleteEnabled)
    }

    // MARK: - 지우기 버튼

    @Test
    fun `입력이 있을 때만 지우기 버튼을 보여준다`() {
        assertTrue(ProfileEditUiState(nickname = "하").isClearVisible)
        assertFalse(ProfileEditUiState(nickname = "").isClearVisible)
    }

    // MARK: - 경고 문구

    @Test
    fun `길이가 어긋나면 한 문구로 안내한다`() {
        // iOS 는 짧을 때와 길 때를 같은 문구로 안내합니다.
        val tooShort = ProfileEditViewModel.warningOf(NicknameValidation.TOO_SHORT)
        val tooLong = ProfileEditViewModel.warningOf(NicknameValidation.TOO_LONG)

        assertEquals("*2~10자로 입력해주세요.", tooShort)
        assertEquals(tooShort, tooLong)
    }

    @Test
    fun `한글이 아니면 한글만 입력하라고 안내한다`() {
        assertEquals("*한글만 입력해주세요.", ProfileEditViewModel.warningOf(NicknameValidation.NOT_KOREAN))
    }

    @Test
    fun `자음 모음만 있으면 올바른 형태로 안내한다`() {
        assertEquals(
            "*올바른 형태로 입력해주세요.",
            ProfileEditViewModel.warningOf(NicknameValidation.INCOMPLETE_KOREAN),
        )
    }

    @Test
    fun `통과하면 문구가 없다`() {
        assertNull(ProfileEditViewModel.warningOf(NicknameValidation.VALID))
    }

    @Test
    fun `모든 경고 문구는 별표로 시작한다`() {
        val warnings = NicknameValidation.entries
            .mapNotNull { ProfileEditViewModel.warningOf(it) } +
            ProfileEditViewModel.DUPLICATED +
            ProfileEditViewModel.SAVE_FAILED

        assertTrue(warnings.all { it.startsWith("*") })
    }
}
