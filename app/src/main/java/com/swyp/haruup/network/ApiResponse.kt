package com.swyp.haruup.network

import kotlinx.serialization.Serializable

/**
 * iOS 의 GenericResponse<T> 와 동일한 공통 응답 래퍼입니다.
 * { "success": Bool, "message": String?, "data": T? }
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
)
