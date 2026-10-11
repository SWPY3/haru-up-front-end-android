package com.swyp.haruup.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 월간 미션 차트 항목. iOS 의 ChartItem 에 대응합니다.
 */
@Serializable
data class ChartItem(
    val rank: Int,
    @SerialName("labelName") val title: String,
    /** 관심사 > 세부 관심사 경로. 화면에는 앞의 두 개만 보여줍니다. */
    @SerialName("interestFullPath") val tags: List<String> = emptyList(),
    @SerialName("selectionCount") val count: Int,
)

/**
 * 랭킹 API 전용 응답입니다.
 *
 * 공통 래퍼인 ApiResponse 와 달리 실패 사유 필드명이 message 가 아니라 errorMessage 입니다.
 * (iOS 의 RankingResponse 와 같은 모양)
 */
@Serializable
data class RankingResponse(
    val success: Boolean,
    val data: List<ChartItem>? = null,
    val errorMessage: String? = null,
)
