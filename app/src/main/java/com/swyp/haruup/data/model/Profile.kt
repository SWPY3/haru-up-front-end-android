package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/**
 * 회원 프로필. iOS 의 ProfileData 에 대응합니다.
 *
 * 마이페이지에서 쓰는 값은 nickname 과 characterId 뿐이지만,
 * 프로필 수정 화면에서 나머지도 쓰게 되므로 응답 형태를 그대로 받아 둡니다.
 */
@Serializable
data class ProfileData(
    val id: Int,
    val memberId: Int,
    val nickname: String,
    val birthDt: String? = null,
    val gender: String? = null,
    val imgId: Int? = null,
    val intro: String? = null,
    val jobId: Int? = null,
    val jobDetailId: Int? = null,
    val characterId: Int,
)
