package com.swyp.haruup.presentation.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 화면에서 고른 한글 태그가 랭킹 API 파라미터로 제대로 바뀌는지 확인합니다.
 * 매핑 테이블은 iOS 의 ChartViewModel 에서 옮겨 온 것이라 값이 어긋나면 두 앱의 결과가 달라집니다.
 */
class ChartFilterTest {

    @Test
    fun `조건이 없으면 limit 만 보낸다`() {
        val query = ChartFilter.toQuery(emptyList())

        assertEquals(5, query.limit)
        assertNull(query.gender)
        assertTrue(query.ageGroups.isEmpty())
        assertTrue(query.jobIds.isEmpty())
        assertTrue(query.jobDetailIds.isEmpty())
        assertTrue(query.interests.isEmpty())
    }

    @Test
    fun `성별을 코드로 바꾼다`() {
        assertEquals("FEMALE", ChartFilter.toQuery(listOf("여성")).gender)
        assertEquals("MALE", ChartFilter.toQuery(listOf("남성")).gender)
    }

    @Test
    fun `성별을 둘 다 고르면 뒤에 오는 값이 남는다`() {
        // gender 는 단일 값이라 덮어쓰기가 됩니다. (iOS 와 같은 동작)
        assertEquals("FEMALE", ChartFilter.toQuery(listOf("남성", "여성")).gender)
    }

    @Test
    fun `연령대는 여러 개를 모아 보낸다`() {
        val query = ChartFilter.toQuery(listOf("20 - 24세", "25 - 29세", "40세 이상"))

        assertEquals(listOf("MID_20S", "LATE_20S", "FORTIES"), query.ageGroups)
    }

    @Test
    fun `연령대 전체는 파라미터를 만들지 않는다`() {
        // 연령대를 안 보내는 것이 곧 전체 조회입니다.
        assertTrue(ChartFilter.toQuery(listOf("전체")).ageGroups.isEmpty())
    }

    @Test
    fun `직업을 id 로 바꾼다`() {
        assertEquals(listOf(1, 3), ChartFilter.toQuery(listOf("직장인", "학생")).jobIds)
    }

    @Test
    fun `세부 직무 하나가 상위 직업 수만큼 id 를 만든다`() {
        val query = ChartFilter.toQuery(listOf("개발자"))

        assertEquals(listOf(3, 12, 21), query.jobDetailIds)
        // 세부 직무는 직업 id 에는 들어가지 않습니다.
        assertTrue(query.jobIds.isEmpty())
    }

    @Test
    fun `세부 직무를 여러 개 고르면 id 가 이어 붙는다`() {
        val query = ChartFilter.toQuery(listOf("디자이너", "기획자"))

        assertEquals(listOf(1, 10, 19, 2, 11, 20), query.jobDetailIds)
    }

    @Test
    fun `관심사는 이름 그대로 보낸다`() {
        val query = ChartFilter.toQuery(listOf("외국어 공부", "재테크/투자"))

        assertEquals(listOf("외국어 공부", "재테크/투자"), query.interests)
    }

    @Test
    fun `섹션에 없는 태그는 무시한다`() {
        val query = ChartFilter.toQuery(listOf("여성", "알 수 없는 값"))

        assertEquals("FEMALE", query.gender)
        assertTrue(query.interests.isEmpty())
        assertTrue(query.jobIds.isEmpty())
    }

    @Test
    fun `여러 섹션을 함께 고르면 각각의 자리에 들어간다`() {
        val query = ChartFilter.toQuery(
            listOf("남성", "30 - 34세", "직장인", "개발자", "체력관리 및 운동"),
        )

        assertEquals("MALE", query.gender)
        assertEquals(listOf("EARLY_30S"), query.ageGroups)
        assertEquals(listOf(1), query.jobIds)
        assertEquals(listOf(3, 12, 21), query.jobDetailIds)
        assertEquals(listOf("체력관리 및 운동"), query.interests)
    }

    @Test
    fun `고른 순서와 상관없이 섹션 순서대로 정렬한다`() {
        val sorted = ChartFilter.sortBySectionOrder(listOf("개발자", "여성", "자격증 공부", "학생"))

        assertEquals(listOf("여성", "학생", "개발자", "자격증 공부"), sorted)
    }

    @Test
    fun `섹션에 없는 태그는 정렬할 때 뒤로 보낸다`() {
        val sorted = ChartFilter.sortBySectionOrder(listOf("알 수 없는 값", "여성"))

        assertEquals(listOf("여성", "알 수 없는 값"), sorted)
    }

    @Test
    fun `관심사 이모지는 모르는 이름이면 기본값을 쓴다`() {
        assertEquals("🌍", ChartFilter.interestIcon("외국어 공부"))
        assertEquals("📌", ChartFilter.interestIcon("헬스"))
    }
}
