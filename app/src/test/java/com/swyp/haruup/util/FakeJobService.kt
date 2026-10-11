package com.swyp.haruup.util

import com.swyp.haruup.data.model.JobDetail
import com.swyp.haruup.network.service.JobService

/** 테스트용 JobService 입니다. 직업 id 별로 세부 직무를 돌려줍니다. */
class FakeJobService(
    var detailsByJob: Map<Int, List<JobDetail>> = emptyMap(),
    /** 이 직업을 조회하면 실패합니다. */
    var failingJobId: Int? = null,
) : JobService {

    val requestedJobIds = mutableListOf<Int>()

    override suspend fun jobDetails(jobId: Int): List<JobDetail> {
        requestedJobIds += jobId
        if (jobId == failingJobId) error("세부 직무를 받아오지 못했습니다.")
        return detailsByJob[jobId].orEmpty()
    }
}
