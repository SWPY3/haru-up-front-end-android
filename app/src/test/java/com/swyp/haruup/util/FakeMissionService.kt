package com.swyp.haruup.util

import com.swyp.haruup.data.model.ChallengeDate
import com.swyp.haruup.data.model.GrowthData
import com.swyp.haruup.data.model.MissionListItem
import com.swyp.haruup.data.model.MissionStatusRequest
import com.swyp.haruup.data.model.MonthlyMissionData
import com.swyp.haruup.data.model.RetryMissionData
import com.swyp.haruup.data.model.RetryMissionRequest
import com.swyp.haruup.data.model.SelectMissionRequest
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.MissionService

/**
 * 테스트용 MissionService 입니다.
 *
 * 호출 횟수를 세어 두어 "같은 달을 두 번 받아오지 않는다" 같은 것을 확인할 수 있고,
 * [shouldFail] 로 실패 상황도 만들 수 있습니다.
 */
class FakeMissionService(
    var missions: List<MissionListItem> = emptyList(),
    var challengeDates: List<ChallengeDate> = emptyList(),
    var monthly: MonthlyMissionData = MonthlyMissionData(),
    var retry: RetryMissionData = RetryMissionData(),
    var shouldFail: Boolean = false,
) : MissionService {

    /** 조회한 달 목록입니다. 같은 달이 두 번 들어오면 캐시가 듣지 않은 것입니다. */
    val requestedMonths = mutableListOf<String>()
    val requestedMissionQueries = mutableListOf<Pair<String, String>>()
    val statusUpdates = mutableListOf<MissionStatusRequest>()
    var selectedRequest: SelectMissionRequest? = null
    var retryRequest: RetryMissionRequest? = null

    private fun <T> respond(data: T): ApiResponse<T> =
        if (shouldFail) ApiResponse(success = false, data = null) else ApiResponse(success = true, data = data)

    override suspend fun missions(
        missionStatus: String,
        targetDate: String,
        memberInterestId: Int?,
    ): ApiResponse<List<MissionListItem>> {
        requestedMissionQueries += missionStatus to targetDate
        return respond(missions)
    }

    override suspend fun retryMissions(request: RetryMissionRequest): ApiResponse<RetryMissionData> {
        retryRequest = request
        return respond(retry)
    }

    override suspend fun selectMissions(request: SelectMissionRequest): ApiResponse<List<Int>> {
        selectedRequest = request
        return respond(request.memberMissionIds)
    }

    override suspend fun updateStatus(request: MissionStatusRequest): ApiResponse<String> {
        statusUpdates += request
        return respond("ok")
    }

    override suspend fun completionStatus(
        startDate: String,
        endDate: String,
    ): ApiResponse<List<ChallengeDate>> = respond(challengeDates)

    override suspend fun monthlyMissions(targetMonth: String): ApiResponse<MonthlyMissionData> {
        requestedMonths += targetMonth
        return respond(monthly)
    }

    override suspend fun growth(
        startTargetMonth: String,
        endTargetMonth: String,
    ): ApiResponse<GrowthData> = respond(GrowthData())
}
