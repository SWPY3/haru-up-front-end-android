package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SnsLoginRequest(
    val provider: String,
    val accessToken: String,
)

@Serializable
data class SnsLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewMember: Boolean = false,
)
