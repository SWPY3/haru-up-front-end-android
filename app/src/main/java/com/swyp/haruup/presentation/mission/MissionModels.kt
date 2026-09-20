package com.swyp.haruup.presentation.mission

import androidx.compose.ui.graphics.Color
import com.swyp.haruup.core.designsystem.HaruUpColor

/**
 * 미션 난이도. iOS 의 MissionDifficultyModel 에 대응합니다.
 * 서버는 1~5 를 쓰고, 챗봇 미션은 1/2/3 스케일만 씁니다.
 */
enum class MissionDifficulty(
    val label: String,
    val backgroundColor: Color,
    val textColor: Color,
) {
    LOW("초급", HaruUpColor.SecondaryMint100, HaruUpColor.SecondaryMint200),
    MEDIUM("중급", HaruUpColor.PrimaryBlue50, HaruUpColor.PrimaryBlue700),
    MEDIUM_HIGH("중급", HaruUpColor.PrimaryBlue50, HaruUpColor.PrimaryBlue700),
    HIGH("고급", HaruUpColor.SecondaryRed100, HaruUpColor.SecondaryRed200),
    VERY_HIGH("고급", HaruUpColor.SecondaryRed100, HaruUpColor.SecondaryRed200),
    ;

    companion object {
        /**
         * 서버 difficulty 값(1~5)을 변환합니다.
         * 챗봇 미션은 1/2/3 스케일을 써서 3 은 "고급"으로 봅니다. (iOS 와 동일)
         */
        fun from(difficulty: Int): MissionDifficulty = when (difficulty) {
            1 -> LOW
            2 -> MEDIUM
            3 -> HIGH
            4 -> HIGH
            5 -> VERY_HIGH
            else -> LOW
        }
    }
}

/** 목록 상단의 난이도 필터. iOS 의 MissionDifficultyFilter 에 대응합니다. */
enum class MissionDifficultyFilter(val label: String) {
    ALL("전체"),
    BEGINNER("초급"),
    INTERMEDIATE("중급"),
    ADVANCED("고급"),
    ;

    fun matches(difficulty: MissionDifficulty): Boolean = when (this) {
        ALL -> true
        BEGINNER -> difficulty == MissionDifficulty.LOW
        INTERMEDIATE ->
            difficulty == MissionDifficulty.MEDIUM || difficulty == MissionDifficulty.MEDIUM_HIGH

        ADVANCED ->
            difficulty == MissionDifficulty.HIGH || difficulty == MissionDifficulty.VERY_HIGH
    }
}

/** 화면에 그릴 미션 한 건 */
data class MissionItem(
    val id: Int,
    val content: String,
    val description: String? = null,
    val difficulty: MissionDifficulty,
    val expEarned: Int,
)
