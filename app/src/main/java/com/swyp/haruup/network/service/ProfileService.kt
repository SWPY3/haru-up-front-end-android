package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.UpdateProfileRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT

/**
 * 프로필 수정. iOS 의 ProfileEditViewModel.requestUpdateProfile 에 대응합니다.
 *
 * 이 엔드포인트만 Authorization 헤더가 아니라 jwt-token 헤더에 refreshToken 을 싣습니다.
 * iOS 가 그렇게 보내고 있어 맞췄습니다.
 *
 * TODO: 서버가 Authorization 헤더도 받는지 확인되면 AuthInterceptor 에 맡기고 이 헤더를 지울 것.
 *  (프로필 조회는 Authorization 을 쓰는데 수정만 jwt-token 을 써서 두 방식이 섞여 있습니다)
 */
interface ProfileService {

    @PUT(ApiPath.Profile.PROFILE)
    suspend fun updateProfile(
        @Header("jwt-token") refreshToken: String,
        @Body request: UpdateProfileRequest,
    ): ApiResponse<Unit>
}
