package com.swyp.haruup.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NicknameValidatorTest {

    @Test
    fun `완성형 한글 두 글자 이상이면 통과한다`() {
        assertNull(NicknameValidator.validate("하루"))
        assertNull(NicknameValidator.validate("영현이네집"))
    }

    @Test
    fun `앞뒤 공백은 길이 계산에서 제외한다`() {
        assertNull(NicknameValidator.validate("  하루  "))
    }

    @Test
    fun `두 글자 미만이면 최소 길이 안내를 돌려준다`() {
        assertEquals("닉네임은 최소 2글자 이상이어야 해요.", NicknameValidator.validate("하"))
        assertEquals("닉네임은 최소 2글자 이상이어야 해요.", NicknameValidator.validate(""))
    }

    @Test
    fun `열 글자를 넘으면 최대 길이 안내를 돌려준다`() {
        assertEquals("닉네임은 최대 10글자까지 가능해요.", NicknameValidator.validate("가나다라마바사아자차카"))
    }

    @Test
    fun `한글이 아닌 문자가 섞이면 한글만 가능하다고 안내한다`() {
        val expected = "한글만 입력이 가능해요.\n다시 입력해주세요."
        assertEquals(expected, NicknameValidator.validate("haru"))
        assertEquals(expected, NicknameValidator.validate("하루1"))
        assertEquals(expected, NicknameValidator.validate("하루!"))
    }

    @Test
    fun `자음이나 모음만 있으면 완성형으로 입력하라고 안내한다`() {
        val expected = "자음이나 모음만으로는 닉네임을 만들 수 없어요.\n완성된 한글로 입력해주세요."
        assertEquals(expected, NicknameValidator.validate("ㅎㄹ"))
        assertEquals(expected, NicknameValidator.validate("하ㅜ"))
    }

    @Test
    fun `길이 검사가 문자 종류 검사보다 먼저다`() {
        // iOS 와 동일한 순서. 한 글자짜리 영문은 길이 안내가 먼저 나온다.
        assertEquals("닉네임은 최소 2글자 이상이어야 해요.", NicknameValidator.validate("a"))
    }
}
