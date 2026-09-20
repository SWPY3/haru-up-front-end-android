package com.swyp.haruup.presentation.home

import androidx.annotation.DrawableRes
import com.swyp.haruup.R

/** 홈 상단에 표시할 회원 정보. iOS 의 HomeMemberInfo 에 대응합니다. */
data class HomeMemberInfo(
    val characterId: Int = 0,
    val level: Int = 1,
    val nickname: String = "",
    val currentExp: Int = 0,
    val maxExp: Int = 0,
    val interest: String = "",
) {
    /** 경험치 진행률. 0 이면 0 으로 두어 0 나누기를 피합니다. */
    val expProgress: Float =
        if (currentExp == 0 || maxExp == 0) 0f else currentExp.toFloat() / maxExp

    /** 예: "꾸준한 하루" */
    val characterDisplayName: String
        get() = "${CharacterLevel.from(level).label} ${if (characterId == 2) "나루" else "하루"}"

    @get:DrawableRes
    val characterImageRes: Int
        get() = characterImageRes(characterId, level)
}

/** 캐릭터 레벨별 수식어. iOS 의 CharacterLevel 에 대응합니다. */
enum class CharacterLevel(val label: String) {
    CHALLENGER("도전하는"),
    GROWING("성장하는"),
    STEADY("꾸준한"),
    PROUD("우쭐한"),
    ;

    companion object {
        fun from(level: Int): CharacterLevel = entries.getOrElse(level - 1) { CHALLENGER }
    }
}

/**
 * 홈 헤더의 캐릭터 이미지는 레벨별로 에셋이 따로 있습니다.
 * 캐릭터 선택 화면(character_*_level1)과는 다른 그림이라 home_ 접두사로 구분합니다.
 * 서버가 아직 없는 레벨을 내려줘도 앱이 깨지지 않게 1~4 밖은 1 로 떨어뜨립니다.
 */
@DrawableRes
private fun characterImageRes(characterId: Int, level: Int): Int {
    val isNaru = characterId == 2
    return when (level.coerceIn(1, 4)) {
        2 -> if (isNaru) R.drawable.home_character_naru_level2 else R.drawable.home_character_haru_level2
        3 -> if (isNaru) R.drawable.home_character_naru_level3 else R.drawable.home_character_haru_level3
        4 -> if (isNaru) R.drawable.home_character_naru_level4 else R.drawable.home_character_haru_level4
        else -> if (isNaru) R.drawable.home_character_naru_level1 else R.drawable.home_character_haru_level1
    }
}
