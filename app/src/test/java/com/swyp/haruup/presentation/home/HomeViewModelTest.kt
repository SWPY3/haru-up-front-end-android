package com.swyp.haruup.presentation.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class HomeViewModelTest {

    @Test
    fun `6시부터 18시까지는 낮으로 본다`() {
        listOf(6, 12, 18).forEach { hour ->
            assertTrue("${hour}시", HomeViewModel.isDaytime(LocalTime.of(hour, 0)))
        }
    }

    @Test
    fun `19시부터 5시까지는 밤으로 본다`() {
        listOf(19, 22, 0, 5).forEach { hour ->
            assertFalse("${hour}시", HomeViewModel.isDaytime(LocalTime.of(hour, 0)))
        }
    }

    @Test
    fun `경계값 확인 - 18시 59분은 낮, 19시 정각은 밤`() {
        assertTrue(HomeViewModel.isDaytime(LocalTime.of(18, 59)))
        assertFalse(HomeViewModel.isDaytime(LocalTime.of(19, 0)))
    }

    @Test
    fun `경험치가 0이면 진행률도 0이다`() {
        assertEquals(0f, HomeMemberInfo(currentExp = 0, maxExp = 250).expProgress, 0.0001f)
    }

    @Test
    fun `최대 경험치가 0이어도 0으로 나누지 않는다`() {
        assertEquals(0f, HomeMemberInfo(currentExp = 50, maxExp = 0).expProgress, 0.0001f)
    }

    @Test
    fun `경험치 진행률을 비율로 계산한다`() {
        assertEquals(0.2f, HomeMemberInfo(currentExp = 50, maxExp = 250).expProgress, 0.0001f)
    }

    @Test
    fun `캐릭터 표시 이름은 레벨 수식어와 캐릭터명을 합친다`() {
        assertEquals("꾸준한 하루", HomeMemberInfo(characterId = 1, level = 3).characterDisplayName)
        assertEquals("우쭐한 나루", HomeMemberInfo(characterId = 2, level = 4).characterDisplayName)
    }

    @Test
    fun `알 수 없는 레벨은 첫 번째 수식어로 떨어진다`() {
        assertEquals("도전하는 하루", HomeMemberInfo(characterId = 1, level = 99).characterDisplayName)
        assertEquals("도전하는 하루", HomeMemberInfo(characterId = 1, level = 0).characterDisplayName)
    }

    @Test
    fun `말풍선 문구는 순환한다`() {
        val state = HomeUiState(memberInfo = HomeMemberInfo(nickname = "영현", interest = "외국어 공부"))
        assertEquals(state.bubbleMessages[0], state.bubbleText)
        assertEquals(state.bubbleMessages[1], state.copy(bubbleIndex = 1).bubbleText)
        // 마지막 다음은 처음으로 돌아온다
        assertEquals(state.bubbleMessages[0], state.copy(bubbleIndex = 3).bubbleText)
    }

    @Test
    fun `닉네임과 관심사가 비어 있으면 기본 문구를 쓴다`() {
        val state = HomeUiState(bubbleIndex = 1)
        assertEquals("사용자님의 외국어 공부을(를) 응원해요!", state.bubbleText)
    }
}
