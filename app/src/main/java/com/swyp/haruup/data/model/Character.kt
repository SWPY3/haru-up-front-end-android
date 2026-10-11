package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/**
 * GET /api/character/list 응답 항목.
 * 이 API 는 다른 API 와 달리 ApiResponse 래퍼 없이 배열을 그대로 내려줍니다.
 */
@Serializable
data class CharacterData(
    val id: Int,
    val name: String? = null,
    val description: String? = null,
)

/** GET /api/character/personality/list 응답 항목 */
@Serializable
data class PersonalityData(
    /** 선택 시 그대로 돌려보낼 값 (WARM_FRIEND / CLEAR_COACH) */
    val code: String,
    /** 사용자에게 보여줄 문구 */
    val label: String,
)

/** POST /api/character/personality 요청 body */
@Serializable
data class SelectPersonalityRequest(
    val personality: String,
)
