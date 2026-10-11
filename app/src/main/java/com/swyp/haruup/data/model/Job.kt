package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/** GET /api/job/getJobDetailList 의 세부 직무 한 건. 서버의 JobDetailDto 에 대응합니다. */
@Serializable
data class JobDetail(
    val id: Int,
    val jobId: Int,
    val jobDetailName: String,
)
