package com.swyp.haruup.util

import com.swyp.haruup.data.model.RankingResponse
import com.swyp.haruup.network.service.ChartService

/** 테스트용 ChartService 입니다. 마지막으로 보낸 조건을 기록합니다. */
class FakeChartService : ChartService {

    var lastJobDetailIds: List<Int>? = null
    var lastAgeGroups: List<String>? = null

    override suspend fun popularRanking(
        limit: Int,
        gender: String?,
        ageGroups: List<String>?,
        jobIds: List<Int>?,
        jobDetailIds: List<Int>?,
        interests: List<String>?,
    ): RankingResponse {
        lastAgeGroups = ageGroups
        lastJobDetailIds = jobDetailIds
        return RankingResponse(success = true, data = emptyList())
    }
}
