package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.JobDetail
import com.swyp.haruup.network.ApiPath
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 직업 / 세부 직무. iOS 의 NetworkDefine.JobAPI 에 대응합니다.
 *
 * 다른 API 와 달리 응답이 ApiResponse 로 감싸져 있지 않고 배열이 그대로 옵니다.
 */
interface JobService {

    @GET(ApiPath.Job.DETAIL_LIST)
    suspend fun jobDetails(@Query("jobId") jobId: Int): List<JobDetail>
}
