package com.swyp.haruup.presentation.history

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GrowthChartTest {

    private fun points(vararg values: Int) =
        values.mapIndexed { index, value -> GrowthPoint("${index + 1}월", value) }

    // MARK: - 스케일

    @Test
    fun `그리드는 항상 5개이고 0에서 시작한다`() {
        listOf(points(), points(0, 0), points(3), points(31), points(200)).forEach { data ->
            val scale = GrowthChartScale.calculate(data)
            assertEquals(5, scale.gridValues.size)
            assertEquals(0, scale.gridValues.first())
        }
    }

    @Test
    fun `데이터가 없거나 모두 0이면 0에서 4까지 쓴다`() {
        listOf(points(), points(0, 0, 0)).forEach { data ->
            val scale = GrowthChartScale.calculate(data)
            assertEquals(4, scale.maxValue)
            assertEquals(listOf(0, 1, 2, 3, 4), scale.gridValues)
        }
    }

    @Test
    fun `최대값은 항상 데이터 최대값보다 크거나 같다`() {
        listOf(1, 3, 7, 12, 28, 31, 99, 150).forEach { max ->
            val scale = GrowthChartScale.calculate(points(max))
            assertTrue("데이터 $max, 축 ${scale.maxValue}", scale.maxValue >= max)
        }
    }

    @Test
    fun `눈금은 떨어지는 숫자를 쓴다`() {
        // 31일 → 여유 1.25배 = 38.75, 4등분 = 9.69 → 10 단위
        val scale = GrowthChartScale.calculate(points(31))
        assertEquals(listOf(0, 10, 20, 30, 40), scale.gridValues)
        assertEquals(40, scale.maxValue)
    }

    @Test
    fun `작은 값도 1 단위로 떨어진다`() {
        val scale = GrowthChartScale.calculate(points(3))
        assertEquals(listOf(0, 1, 2, 3, 4), scale.gridValues)
    }

    @Test
    fun `100을 넘으면 50 단위로 올린다`() {
        val scale = GrowthChartScale.calculate(points(500))
        assertEquals(0, scale.maxValue % 50)
        assertTrue(scale.maxValue >= 500)
    }

    // MARK: - 안내 문구

    @Test
    fun `늘어난 경우 늘어난 일수를 강조한다`() {
        val result = growthDescription(points(3, 7))
        assertEquals("지난달보다 미션을 완료한 날이 4일 늘었어요.", result.text)
        assertEquals("4", result.highlight)
    }

    @Test
    fun `줄어든 경우 줄어든 일수를 강조한다`() {
        val result = growthDescription(points(7, 3))
        assertEquals("지난달보다 미션을 완료한 날이 4일 줄었어요.", result.text)
        assertEquals("4", result.highlight)
    }

    @Test
    fun `같은 경우 이번 달 일수를 강조한다`() {
        val result = growthDescription(points(5, 5))
        assertEquals("지난달과 미션을 완료한 날이 5일로 같아요.", result.text)
        assertEquals("5", result.highlight)
    }

    @Test
    fun `데이터가 하나뿐이면 지난달을 0으로 본다`() {
        val result = growthDescription(points(3))
        assertEquals("지난달보다 미션을 완료한 날이 3일 늘었어요.", result.text)
    }

    @Test
    fun `데이터가 없으면 0일로 같다고 안내한다`() {
        assertEquals("지난달과 미션을 완료한 날이 0일로 같아요.", growthDescription(points()).text)
    }
}
