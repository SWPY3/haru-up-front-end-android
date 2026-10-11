package com.swyp.haruup.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 검사 결과가 이유별로 갈리는지 확인합니다.
 * 화면마다 다른 문구를 붙이려면 이유가 정확해야 합니다.
 */
class NicknameValidationTest {

    @Test
    fun `두 글자 미만은 짧다고 본다`() {
        assertEquals(NicknameValidation.TOO_SHORT, NicknameValidator.check("하"))
        assertEquals(NicknameValidation.TOO_SHORT, NicknameValidator.check(""))
    }

    @Test
    fun `열 글자를 넘으면 길다고 본다`() {
        assertEquals(NicknameValidation.TOO_LONG, NicknameValidator.check("가".repeat(11)))
    }

    @Test
    fun `열 글자까지는 통과한다`() {
        assertEquals(NicknameValidation.VALID, NicknameValidator.check("가".repeat(10)))
    }

    @Test
    fun `한글이 아닌 글자가 섞이면 거른다`() {
        assertEquals(NicknameValidation.NOT_KOREAN, NicknameValidator.check("하루up"))
        assertEquals(NicknameValidation.NOT_KOREAN, NicknameValidator.check("하루123"))
    }

    @Test
    fun `자음이나 모음만 있으면 거른다`() {
        assertEquals(NicknameValidation.INCOMPLETE_KOREAN, NicknameValidator.check("하ㄹ"))
    }

    @Test
    fun `앞뒤 공백은 길이에서 빼고 센다`() {
        assertEquals(NicknameValidation.TOO_SHORT, NicknameValidator.check("  하  "))
        assertEquals(NicknameValidation.VALID, NicknameValidator.check("  하루  "))
    }
}
