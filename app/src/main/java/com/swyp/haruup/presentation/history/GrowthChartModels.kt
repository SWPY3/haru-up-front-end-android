package com.swyp.haruup.presentation.history

import kotlin.math.abs
import kotlin.math.ceil

/** 성장 차트의 한 점. iOS 의 GrowthChartDataPoint 에 대응합니다. */
data class GrowthPoint(
    /** 예: "8월" */
    val monthLabel: String,
    /** 그 달에 미션을 완료한 날 수 */
    val value: Int,
)

/**
 * Y축 스케일. iOS 의 GrowthChartScale 에 대응합니다.
 * 그리드는 항상 5개(0 포함 4등분)입니다.
 */
data class GrowthChartScale(
    val maxValue: Int,
    val gridValues: List<Int>,
) {
    companion object {
        /** 그리드 선 개수. 0 부터 시작해 4등분합니다. */
        private const val GRID_COUNT = 5

        /** 말풍선이 들어갈 위쪽 공간을 확보하려고 데이터 최대값보다 여유를 둡니다. */
        private const val HEADROOM_RATIO = 1.25

        /** 눈금을 떨어지는 숫자로 맞춥니다. */
        private val NICE_INTERVALS = listOf(1, 2, 5, 10, 15, 20, 25, 50, 100)

        fun calculate(points: List<GrowthPoint>): GrowthChartScale {
            val dataMax = points.maxOfOrNull { it.value } ?: 0

            if (dataMax <= 0) {
                return GrowthChartScale(maxValue = 4, gridValues = listOf(0, 1, 2, 3, 4))
            }

            val interval = niceInterval(dataMax * HEADROOM_RATIO / (GRID_COUNT - 1))

            return GrowthChartScale(
                maxValue = interval * (GRID_COUNT - 1),
                gridValues = List(GRID_COUNT) { it * interval },
            )
        }

        /** 100 을 넘으면 50 단위로 올립니다. */
        private fun niceInterval(value: Double): Int =
            NICE_INTERVALS.firstOrNull { it >= value } ?: (ceil(value / 50.0).toInt() * 50)
    }
}

/**
 * 지난달과 비교한 안내 문구를 만듭니다.
 * 강조할 숫자를 함께 돌려주어 그 부분만 다른 색으로 칠할 수 있게 합니다.
 */
data class GrowthDescription(
    val text: String,
    val highlight: String,
)

fun growthDescription(points: List<GrowthPoint>): GrowthDescription {
    val current = points.lastOrNull()?.value ?: 0
    val previous = points.dropLast(1).lastOrNull()?.value ?: 0
    val difference = current - previous

    return when {
        difference > 0 -> GrowthDescription(
            text = "지난달보다 미션을 완료한 날이 ${difference}일 늘었어요.",
            highlight = "$difference",
        )

        difference < 0 -> GrowthDescription(
            text = "지난달보다 미션을 완료한 날이 ${abs(difference)}일 줄었어요.",
            highlight = "${abs(difference)}",
        )

        else -> GrowthDescription(
            text = "지난달과 미션을 완료한 날이 ${current}일로 같아요.",
            highlight = "$current",
        )
    }
}
