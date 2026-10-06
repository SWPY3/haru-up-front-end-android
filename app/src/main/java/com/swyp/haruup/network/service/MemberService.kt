package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.HomeMemberInfoData
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.POST

/**
 * iOS 의 MemberService 에 대응합니다.
 */
interface MemberService {

    /**
     * 홈 상단 회원 정보입니다.
     *
     * 조회인데도 POST 를 씁니다. 서버가 그렇게 받고 있어 iOS 도 POST 로 보냅니다.
     * data 는 배열로 오지만 첫 번째만 씁니다.
     */
    @POST(ApiPath.Member.HOME_INFO)
    suspend fun homeMemberInfo(): ApiResponse<List<HomeMemberInfoData>>
}
