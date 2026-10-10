package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.ChallengeDate
import com.swyp.haruup.data.model.GrowthData
import com.swyp.haruup.data.model.MissionListItem
import com.swyp.haruup.data.model.MissionStatusRequest
import com.swyp.haruup.data.model.MonthlyMissionData
import com.swyp.haruup.data.model.RetryMissionData
import com.swyp.haruup.data.model.RetryMissionRequest
import com.swyp.haruup.data.model.SelectMissionRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * iOS 의 MissionService 에 대응합니다.
 */
interface MissionService {

    /**
     * 날짜별 미션 목록입니다.
     *
     * @param missionStatus 쉼표로 이어 붙입니다. 예: "COMPLETED,ACTIVE"
     * @param memberInterestId 비워 두면 그날 전체 미션이 옵니다. (iOS 도 홈에서는 비웁니다)
     */
    @GET(ApiPath.Mission.LIST)
    suspend fun missions(
        @Query("missionStatus") missionStatus: String,
        @Query("targetDate") targetDate: String,
        @Query("memberInterestId") memberInterestId: Int? = null,
    ): ApiResponse<List<MissionListItem>>

    /** 다른 미션으로 다시 추천받습니다. 하루에 쓸 수 있는 횟수가 정해져 있습니다. */
    @POST(ApiPath.Mission.RETRY)
    suspend fun retryMissions(@Body request: RetryMissionRequest): ApiResponse<RetryMissionData>

    /** 고른 미션을 확정합니다. */
    @POST(ApiPath.Mission.SELECT)
    suspend fun selectMissions(@Body request: SelectMissionRequest): ApiResponse<List<Int>>

    /** 미션을 완료하거나 지웁니다. 상태 값만 다릅니다. */
    @PUT(ApiPath.Mission.STATUS)
    suspend fun updateStatus(@Body request: MissionStatusRequest): ApiResponse<String>

    /** 기간 안의 날짜별 달성 여부입니다. 연속 달성일을 세는 데 씁니다. */
    @GET(ApiPath.Mission.COMPLETION_STATUS)
    suspend fun completionStatus(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): ApiResponse<List<ChallengeDate>>

    /** 그 달의 일별 미션 현황입니다. (기록 탭 캘린더) */
    @GET("${ApiPath.Mission.MONTHLY}/{targetMonth}")
    suspend fun monthlyMissions(
        @Path("targetMonth") targetMonth: String,
    ): ApiResponse<MonthlyMissionData>

    /** 기간 안의 월별 달성일수입니다. (기록 탭 성장 차트) */
    @GET(ApiPath.Mission.MONTHLY)
    suspend fun growth(
        @Query("startTargetMonth") startTargetMonth: String,
        @Query("endTargetMonth") endTargetMonth: String,
    ): ApiResponse<GrowthData>
}
