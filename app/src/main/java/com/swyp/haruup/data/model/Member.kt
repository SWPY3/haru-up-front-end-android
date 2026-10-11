package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/**
 * 홈 상단에 쓰는 회원 정보. iOS 의 Member.HomeMemberInfo 에 대응합니다.
 *
 * totalExp 도 내려오지만 iOS 에서도 화면에 쓰지 않아 받지 않습니다.
 */
@Serializable
data class HomeMemberInfoData(
    val characterId: Int = 1,
    val levelNumber: Int = 1,
    val nickname: String = "",
    val currentExp: Int = 0,
    val maxExp: Int = 0,
    /**
     * 관심사 경로 목록입니다. 한 사람이 여러 관심사를 가질 수 있어 두 겹입니다.
     * 화면에는 첫 관심사의 가장 큰 분류만 씁니다.
     */
    val interests: List<List<String>> = emptyList(),
)
