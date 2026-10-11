package com.swyp.haruup.presentation.chart

import com.swyp.haruup.data.model.JobDetail

/** 필터 바텀시트의 섹션 하나입니다. */
data class ChartFilterSection(
    val title: String,
    val tags: List<String>,
)

/** 세부 직무 이름 → 서버 id 목록 */
typealias JobDetailIds = Map<String, List<Int>>

/**
 * 랭킹 API 에 넘길 조회 조건입니다.
 * iOS 의 convertTagsToParameters 가 만들던 딕셔너리를 타입으로 옮겼습니다.
 */
data class ChartRankingQuery(
    val limit: Int = ChartFilter.LIMIT,
    val gender: String? = null,
    val ageGroups: List<String> = emptyList(),
    val jobIds: List<Int> = emptyList(),
    val jobDetailIds: List<Int> = emptyList(),
    val interests: List<String> = emptyList(),
)

/**
 * 화면에서 고른 한글 태그를 API 파라미터로 바꿉니다.
 * iOS 의 ChartViewModel 안에 있던 매핑 테이블에서 출발했지만, 값은 서버(haru-up-back-end-2)에 맞췄습니다.
 */
object ChartFilter {

    /** 월간 미션 차트는 TOP5 입니다. */
    const val LIMIT = 5

    private const val JOB_DETAIL_TITLE = "세부 직무"

    private val GENDER = mapOf("남성" to "MALE", "여성" to "FEMALE")

    /**
     * 서버 AgeGroup 구간을 그대로 씁니다.
     *
     * "34~36세" 는 서버 표기(33~36세)와 다릅니다. 서버가 33세를 EARLY_30S 로 먼저 분류해
     * 실제로는 34세부터 들어가기 때문에 화면에는 겹치지 않게 적습니다.
     *
     * "전체" 는 일부러 빠져 있습니다.
     * 연령대 파라미터를 아예 보내지 않는 것이 곧 전체 조회라서 따로 값이 필요 없습니다. (iOS 와 동일)
     */
    private val AGE_GROUP = linkedMapOf(
        "10대" to "TEEN",
        "20~22세" to "EARLY_20S",
        "23~26세" to "MID_20S",
        "27~29세" to "LATE_20S",
        "30~33세" to "EARLY_30S",
        "34~36세" to "MID_30S",
        "37~39세" to "LATE_30S",
        "40대" to "FORTIES",
        "50대 이상" to "FIFTIES_PLUS",
    )

    /** 서버 job 테이블의 id 와 같습니다. 세부 직무를 받아올 때도 이 id 를 씁니다. */
    val JOB_ID = linkedMapOf("직장인" to 1, "자영업" to 2, "학생" to 3, "취준생" to 4)

    /** 서버가 관심사 대분류 이름 그대로 비교하므로 서버 데이터와 글자까지 같아야 합니다. */
    private val INTERESTS = listOf(
        "외국어 공부", "자격증 공부", "재테크 및 투자", "체력관리 및 운동", "직무 관련 역량 개발",
    )

    /**
     * 시트에 보일 섹션입니다.
     *
     * 세부 직무는 서버에서 받아온 것만 보여 줍니다. 못 받아왔으면 섹션을 감춥니다.
     * 엉뚱한 id 로 조회하느니 고를 수 없게 하는 편이 낫기 때문입니다.
     */
    fun sections(jobDetails: JobDetailIds = emptyMap()): List<ChartFilterSection> = buildList {
        add(ChartFilterSection("성별", GENDER.keys.toList()))
        add(ChartFilterSection("연령대", listOf("전체") + AGE_GROUP.keys))
        add(ChartFilterSection("직업", JOB_ID.keys.toList()))
        if (jobDetails.isNotEmpty()) add(ChartFilterSection(JOB_DETAIL_TITLE, jobDetails.keys.toList()))
        add(ChartFilterSection("관심사", INTERESTS))
    }

    /**
     * 화면에서 고른 태그를 API 파라미터로 바꿉니다. 어느 섹션에도 없는 태그는 무시합니다.
     *
     * @param jobDetails 세부 직무 이름 → id. 같은 이름이 여러 직업에 있으면 id 를 모두 보냅니다.
     */
    fun toQuery(tags: List<String>, jobDetails: JobDetailIds = emptyMap()): ChartRankingQuery {
        var gender: String? = null
        val ageGroups = mutableListOf<String>()
        val jobIds = mutableListOf<Int>()
        val jobDetailIds = mutableListOf<Int>()
        val interests = mutableListOf<String>()

        tags.forEach { tag ->
            when {
                GENDER.containsKey(tag) -> gender = GENDER[tag]
                AGE_GROUP.containsKey(tag) -> ageGroups += AGE_GROUP.getValue(tag)
                JOB_ID.containsKey(tag) -> jobIds += JOB_ID.getValue(tag)
                jobDetails.containsKey(tag) -> jobDetailIds += jobDetails.getValue(tag)
                tag in INTERESTS -> interests += tag
            }
        }

        return ChartRankingQuery(
            gender = gender,
            ageGroups = ageGroups,
            jobIds = jobIds,
            jobDetailIds = jobDetailIds,
            interests = interests,
        )
    }

    /**
     * 받아온 세부 직무를 이름별 id 로 묶습니다. 직업 순서, 받아온 순서를 그대로 지킵니다.
     * 서버 데이터에 같은 이름이 여러 번 있어도 칩은 하나만 보이고 id 는 모두 보냅니다.
     */
    fun groupJobDetails(details: List<JobDetail>): JobDetailIds =
        details.groupBy({ it.jobDetailName }, { it.id })

    /**
     * 고른 태그를 섹션에 적힌 순서대로 정렬합니다.
     *
     * iOS 는 섹션을 위에서부터 훑어 선택된 태그를 모으기 때문에
     * 사용자가 어떤 순서로 눌렀든 칩은 늘 섹션 순서로 보입니다. 그 동작을 맞춘 것입니다.
     */
    fun sortBySectionOrder(
        tags: Collection<String>,
        sections: List<ChartFilterSection> = sections(),
    ): List<String> {
        val order = sections.flatMap { it.tags }
        return tags.sortedBy { tag -> order.indexOf(tag).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }

    /**
     * 관심사 이름 앞에 붙는 이모지입니다. iOS 의 Interest.iconForInterest 와 같습니다.
     * TODO: 관심사 모델을 이관하면 그쪽으로 옮기기
     */
    fun interestIcon(name: String): String = when (name) {
        "외국어 공부" -> "🌍"
        "체력관리 및 운동" -> "🏋🏻‍♀️"
        "재테크 및 투자" -> "💵"
        "자격증 공부" -> "🪪"
        "직무 관련 역량 개발" -> "👩🏻‍💻"
        else -> "📌"
    }
}
