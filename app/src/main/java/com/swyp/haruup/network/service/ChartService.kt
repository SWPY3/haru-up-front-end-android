package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.RankingResponse
import com.swyp.haruup.network.ApiPath
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * iOS 의 ChartService 에 대응합니다.
 *
 * 배열 파라미터는 Retrofit 이 `ageGroups=A&ageGroups=B` 형태로 붙입니다.
 * iOS 가 쓰는 URLEncoding(arrayEncoding: .noBrackets) 과 같은 형식입니다.
 * null 이거나 빈 리스트면 파라미터 자체가 빠집니다.
 */
interface ChartService {

    @GET(ApiPath.Ranking.POPULAR)
    suspend fun popularRanking(
        @Query("limit") limit: Int,
        @Query("gender") gender: String? = null,
        @Query("ageGroups") ageGroups: List<String>? = null,
        @Query("jobIds") jobIds: List<Int>? = null,
        @Query("jobDetailIds") jobDetailIds: List<Int>? = null,
        @Query("interests") interests: List<String>? = null,
    ): RankingResponse
}
