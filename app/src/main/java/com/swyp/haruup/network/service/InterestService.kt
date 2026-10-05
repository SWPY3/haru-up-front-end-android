package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.InterestListResponse
import com.swyp.haruup.data.model.MemberInterestResponse
import com.swyp.haruup.data.model.UpdateMemberInterestRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * iOS 의 InterestsService 에 대응합니다.
 *
 * 관심사 / 세부 관심사 / 목표가 같은 경로를 쓰고 parentId 로만 갈립니다.
 * parentId 가 없으면 최상위 관심사 목록입니다.
 */
interface InterestService {

    @GET(ApiPath.Interest.DATA)
    suspend fun interests(@Query("parentId") parentId: Int? = null): InterestListResponse

    /** 회원이 지금 고른 관심사 경로를 받아옵니다. */
    @GET(ApiPath.Interest.MEMBER)
    suspend fun memberInterests(): MemberInterestResponse

    @PUT("${ApiPath.Interest.MEMBER}/{memberInterestId}")
    suspend fun updateMemberInterest(
        @Path("memberInterestId") memberInterestId: Int,
        @Body request: UpdateMemberInterestRequest,
    ): ApiResponse<Unit>
}
