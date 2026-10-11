package com.swyp.haruup.presentation.chart

import com.swyp.haruup.data.model.JobDetail
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 화면에서 고른 한글 태그가 랭킹 API 파라미터로 제대로 바뀌는지 확인합니다.
 * 값은 서버(haru-up-back-end-2)의 AgeGroup / job 데이터에 맞춰야 합니다. 어긋나면 400 이나 엉뚱한 결과가 나옵니다.
 */
class ChartFilterTest {

    /** 서버 seed(scripts/job.sql) 일부와 같은 모양 */
    private val jobDetails = ChartFilter.groupJobDetails(
        listOf(
            JobDetail(id = 1, jobId = 1, jobDetailName = "사무직"),
            JobDetail(id = 2, jobId = 1, jobDetailName = "IT/개발자"),
            JobDetail(id = 7, jobId = 2, jobDetailName = "카페/음식점 운영"),
            // 서버 데이터에 같은 이름이 두 번 들어가 있습니다.
            JobDetail(id = 12, jobId = 2, jobDetailName = "카페/음식점 운영"),
            JobDetail(id = 19, jobId = 3, jobDetailName = "대학생"),
        )
    )

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
    fun `연령대는 서버 구간 코드로 모아 보낸다`() {
        val query = ChartFilter.toQuery(listOf("10대", "23~26세", "50대 이상"))

        assertEquals(listOf("TEEN", "MID_20S", "FIFTIES_PLUS"), query.ageGroups)
    }

    @Test
    fun `연령대 칩은 서버 AgeGroup 을 빠짐없이 덮는다`() {
        val ageTags = ChartFilter.sections().first { it.title == "연령대" }.tags - "전체"
        val codes = ChartFilter.toQuery(ageTags).ageGroups

        assertEquals(
            listOf("TEEN", "EARLY_20S", "MID_20S", "LATE_20S", "EARLY_30S", "MID_30S", "LATE_30S", "FORTIES", "FIFTIES_PLUS"),
            codes,
        )
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
    fun `세부 직무는 서버에서 받은 id 로 바꾼다`() {
        val query = ChartFilter.toQuery(listOf("IT/개발자", "대학생"), jobDetails)

        assertEquals(listOf(2, 19), query.jobDetailIds)
        // 세부 직무는 직업 id 에는 들어가지 않습니다.
        assertTrue(query.jobIds.isEmpty())
    }

    @Test
    fun `이름이 같은 세부 직무는 칩 하나로 보이고 id 는 모두 보낸다`() {
        val section = ChartFilter.sections(jobDetails).first { it.title == "세부 직무" }

        assertEquals(1, section.tags.count { it == "카페/음식점 운영" })
        assertEquals(listOf(7, 12), ChartFilter.toQuery(listOf("카페/음식점 운영"), jobDetails).jobDetailIds)
    }

    @Test
    fun `세부 직무를 못 받아왔으면 섹션을 감춘다`() {
        assertFalse(ChartFilter.sections().any { it.title == "세부 직무" })
        // 받아오기 전에 고른 이름은 보낼 id 가 없으니 무시합니다.
        assertTrue(ChartFilter.toQuery(listOf("IT/개발자")).jobDetailIds.isEmpty())
    }

    @Test
    fun `세부 직무 섹션은 직업과 관심사 사이에 둔다`() {
        val titles = ChartFilter.sections(jobDetails).map { it.title }

        assertEquals(listOf("성별", "연령대", "직업", "세부 직무", "관심사"), titles)
    }

    @Test
    fun `관심사는 이름 그대로 보낸다`() {
        val query = ChartFilter.toQuery(listOf("외국어 공부", "재테크 및 투자"))

        assertEquals(listOf("외국어 공부", "재테크 및 투자"), query.interests)
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
            listOf("남성", "30~33세", "직장인", "사무직", "체력관리 및 운동"),
            jobDetails,
        )

        assertEquals("MALE", query.gender)
        assertEquals(listOf("EARLY_30S"), query.ageGroups)
        assertEquals(listOf(1), query.jobIds)
        assertEquals(listOf(1), query.jobDetailIds)
        assertEquals(listOf("체력관리 및 운동"), query.interests)
    }

    @Test
    fun `고른 순서와 상관없이 섹션 순서대로 정렬한다`() {
        val sorted = ChartFilter.sortBySectionOrder(
            listOf("IT/개발자", "여성", "자격증 공부", "학생"),
            ChartFilter.sections(jobDetails),
        )

        assertEquals(listOf("여성", "학생", "IT/개발자", "자격증 공부"), sorted)
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
