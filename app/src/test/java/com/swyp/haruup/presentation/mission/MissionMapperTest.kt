package com.swyp.haruup.presentation.mission

import com.swyp.haruup.data.model.MissionListItem
import com.swyp.haruup.data.model.MissionStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * 서버 응답을 화면 모델로 바꾸는 규칙을 고정합니다.
 */
class MissionMapperTest {

    private fun item(difficulty: Int, description: String? = null) = MissionListItem(
        id = 7,
        missionStatus = MissionStatus.ACTIVE,
        missionContent = "영어 단어 10개 외우기",
        missionDescription = description,
        difficulty = difficulty,
        expEarned = 100,
    )

    @Test
    fun `내용과 경험치를 그대로 옮긴다`() {
        val mission = item(difficulty = 2).toMissionItem()

        assertEquals(7, mission.id)
        assertEquals("영어 단어 10개 외우기", mission.content)
        assertEquals(100, mission.expEarned)
    }

    @Test
    fun `난이도 숫자를 등급으로 바꾼다`() {
        assertEquals(MissionDifficulty.LOW, item(1).toMissionItem().difficulty)
        assertEquals(MissionDifficulty.MEDIUM, item(2).toMissionItem().difficulty)
        assertEquals(MissionDifficulty.HIGH, item(3).toMissionItem().difficulty)
    }

    @Test
    fun `관심사 기반 미션은 설명이 없다`() {
        // 챗봇이 만든 미션만 설명을 가집니다.
        assertNull(item(1).toMissionItem().description)
        assertEquals("매일 아침 10분", item(1, "매일 아침 10분").toMissionItem().description)
    }
}
