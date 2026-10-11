package com.swyp.haruup.presentation.mission

/**
 * 추천받을 미션이 어디서 왔는지. iOS 의 MissionSource 에 대응합니다.
 *
 * 챗봇 목표로 만든 미션은 서버가 memberInterestId 를 0 으로 둡니다.
 * 관심사를 하나도 고르지 않았다면 챗봇 미션으로 봅니다.
 */
sealed interface MissionSource {

    data object Chatbot : MissionSource

    data class Interest(val memberInterestIds: List<Int>) : MissionSource

    /** 추천 API 에 보낼 관심사 ID 목록 */
    val recommendIds: List<Int>
        get() = when (this) {
            Chatbot -> listOf(CHATBOT_INTEREST_ID)
            is Interest -> memberInterestIds
        }

    /** 재추천은 관심사 하나씩만 받습니다. 여러 개면 첫 번째를 씁니다. (iOS 와 동일) */
    val retryId: Int
        get() = when (this) {
            Chatbot -> CHATBOT_INTEREST_ID
            is Interest -> memberInterestIds.firstOrNull() ?: CHATBOT_INTEREST_ID
        }

    /** 관심사 미션일 때만 고른 미션을 빼 달라고 알립니다. 챗봇 미션은 서버가 받지 않습니다. */
    val sendsExcludeIds: Boolean
        get() = this is Interest

    companion object {
        /** 챗봇 목표로 만든 미션을 가리키는 값입니다. */
        const val CHATBOT_INTEREST_ID = 0

        fun from(memberInterestIds: List<Int>): MissionSource {
            val interestIds = memberInterestIds.filter { it != CHATBOT_INTEREST_ID }
            return if (interestIds.isEmpty()) Chatbot else Interest(interestIds)
        }
    }
}
