package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/** 로그인 수단. 서버가 받는 값입니다. iOS 의 SocialLoginProvider 와 같습니다. */
enum class SocialLoginProvider(val value: String) {
    KAKAO("KAKAO"),
    NAVER("NAVER"),
    APPLE("APPLE"),
}

/**
 * POST /api/member/auth/sns-login 요청 body. iOS 의 SocialLoginRequestDTO 에 대응합니다.
 *
 * SNS 액세스 토큰이 아니라 SNS 가 알려 준 사용자 식별자와 프로필을 보냅니다.
 * 애플 전용 필드는 값이 없으면 빠집니다. (Json 의 explicitNulls = false)
 */
@Serializable
data class SnsLoginRequest(
    val loginType: String,
    val snsId: String,
    val email: String = "",
    val name: String = "",
    val identityToken: String? = null,
    val authorizationCode: String? = null,
    val nonce: String? = null,
)

/** sns-login 응답의 data. iOS 의 SocialLoginResponseDTO.AuthData 에 대응합니다. */
@Serializable
data class SnsLoginResponse(
    /** 회원 식별자. 다른 계정으로 로그인했는지 판단하는 데 씁니다. */
    val id: Int,
    val accessToken: String,
    val refreshToken: String,
    val name: String? = null,
    val email: String? = null,
    val loginType: String = "",
    val snsId: String? = null,
    val status: String = "",
)
