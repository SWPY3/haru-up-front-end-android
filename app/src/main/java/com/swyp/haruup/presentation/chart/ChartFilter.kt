package com.swyp.haruup.presentation.chart

/** 필터 바텀시트의 섹션 하나입니다. */
data class ChartFilterSection(
    val title: String,
    val tags: List<String>,
)

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
 * iOS 의 ChartViewModel 안에 있던 매핑 테이블을 그대로 옮겼습니다.
 */
object ChartFilter {

    /** 월간 미션 차트는 TOP5 입니다. */
    const val LIMIT = 5

    val SECTIONS = listOf(
        ChartFilterSection("성별", listOf("남성", "여성")),
        ChartFilterSection(
            "연령대",
            listOf("전체", "19세 이하", "20 - 24세", "25 - 29세", "30 - 34세", "35 - 39세", "40세 이상"),
        ),
        ChartFilterSection("직업", listOf("직장인", "자영업", "학생", "취준생")),
        ChartFilterSection(
            "세부 직무",
            listOf("디자이너", "기획자", "개발자", "사무직", "서비스직", "교육 종사자", "의료직", "공공·복지", "예체능"),
        ),
        ChartFilterSection(
            "관심사",
            listOf("외국어 공부", "자격증 공부", "재테크/투자", "체력관리 및 운동", "직무 관련 역량 개발"),
        ),
    )

    private val GENDER = mapOf("남성" to "MALE", "여성" to "FEMALE")

    /**
     * "전체" 는 일부러 빠져 있습니다.
     * 연령대 파라미터를 아예 보내지 않는 것이 곧 전체 조회라서 따로 값이 필요 없습니다. (iOS 와 동일)
     */
    private val AGE_GROUP = mapOf(
        "19세 이하" to "UNDER_19",
        "20 - 24세" to "MID_20S",
        "25 - 29세" to "LATE_20S",
        "30 - 34세" to "EARLY_30S",
        "35 - 39세" to "LATE_30S",
        "40세 이상" to "FORTIES",
    )

    private val JOB_ID = mapOf("직장인" to 1, "자영업" to 2, "학생" to 3, "취준생" to 4)

    /**
     * 세부 직무는 상위 직업마다 id 가 따로 있어서 한 이름이 여러 id 를 가집니다.
     * TODO: 세부 직무 / 관심사 id 를 서버에서 받아오도록 교체 (iOS 에도 같은 TODO 가 있습니다)
     */
    private val JOB_DETAIL_IDS = mapOf(
        "디자이너" to listOf(1, 10, 19),
        "기획자" to listOf(2, 11, 20),
        "개발자" to listOf(3, 12, 21),
        "사무직" to listOf(4, 13, 22),
        "서비스직" to listOf(5, 14, 23),
        "교육 종사자" to listOf(6, 15, 24),
        "의료직" to listOf(7, 16, 25),
        "공공·복지" to listOf(8, 17, 26),
        "예체능" to listOf(9, 18, 27),
    )

    private val INTERESTS = setOf(
        "외국어 공부", "자격증 공부", "재테크/투자", "체력관리 및 운동", "직무 관련 역량 개발",
    )

    /** 어느 섹션에도 없는 태그는 그냥 무시합니다. */
    fun toQuery(tags: List<String>): ChartRankingQuery {
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
                JOB_DETAIL_IDS.containsKey(tag) -> jobDetailIds += JOB_DETAIL_IDS.getValue(tag)
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

    /** 섹션에 적힌 순서를 미리 펼쳐 둡니다. */
    private val TAG_ORDER = SECTIONS.flatMap { it.tags }

    /**
     * 고른 태그를 섹션에 적힌 순서대로 정렬합니다.
     *
     * iOS 는 섹션을 위에서부터 훑어 선택된 태그를 모으기 때문에
     * 사용자가 어떤 순서로 눌렀든 칩은 늘 섹션 순서로 보입니다. 그 동작을 맞춘 것입니다.
     */
    fun sortBySectionOrder(tags: Collection<String>): List<String> =
        tags.sortedBy { tag -> TAG_ORDER.indexOf(tag).takeIf { it >= 0 } ?: Int.MAX_VALUE }

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
