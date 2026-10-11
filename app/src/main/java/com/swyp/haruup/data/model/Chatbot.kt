package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/** POST /api/member/curation/chatbot/answer 요청 body */
@Serializable
data class ChatbotAnswerRequest(
    val sessionId: String,
    val answer: String,
)

/** POST /api/member/profile/nickName_duplicate_check 요청 body */
@Serializable
data class NicknameDuplicateRequest(
    val nickName: String,
)

/** POST /api/member/curation/chatbot/start 응답 */
@Serializable
data class ChatbotStartData(
    val sessionId: String,
    val question: String,
    /**
     * 첫 질문에서는 항상 비어 있습니다.
     * 사용자가 예시를 그대로 골라 목표가 구체화되지 않는 문제 때문에 서버가 선택지를 주지 않습니다.
     */
    val examples: List<String> = emptyList(),
    /** 입력창에 표시할 예시 (고를 수 없음) */
    val placeholder: String? = null,
    val questionNumber: Int = 0,
)

/**
 * answer API 는 네 가지 응답이 올 수 있어 하나의 모델로 받습니다.
 * 어떤 응답인지는 [kind] 로 판별합니다. (iOS ChatbotAnswerResultData 와 동일)
 */
@Serializable
data class ChatbotAnswerData(
    // 공통
    val sessionId: String? = null,

    // 진행 중 (다음 꼬리질문 또는 투자 가능 시간 질문)
    val question: String? = null,
    val examples: List<String>? = null,
    val questionNumber: Int? = null,
    val isLast: Boolean? = null,
    val questionType: String? = null,

    // 목표 검증 실패 (목표를 2개 이상 입력한 경우)
    val isValidGoal: Boolean? = null,
    val message: String? = null,
    val detectedGoals: List<String>? = null,

    // 마무리 확인 (정보가 충분히 모였을 때)
    val awaitingFinishConfirmation: Boolean? = null,
    val answeredCount: Int? = null,

    // 완료
    val isCompleted: Boolean? = null,
    val goalText: String? = null,
    val missions: List<ChatbotMission>? = null,

    /** 마무리 확인과 완료 응답 모두 사용자에게 보여줄 짧은 요약을 내려줍니다. */
    val summary: String? = null,
) {
    /** 서버 응답 종류 */
    enum class Kind {
        /** 목표를 하나만 입력하도록 되돌리기 */
        GOAL_REJECTED,

        /** 요약을 보여주고 마무리할지 묻기 */
        FINISH_CONFIRM,

        /** 다음 질문 표시 */
        NEXT_QUESTION,

        /** 대화 종료, 미션 생성 완료 */
        COMPLETED,

        UNKNOWN,
    }

    val kind: Kind
        get() = when {
            isCompleted == true -> Kind.COMPLETED
            isValidGoal == false -> Kind.GOAL_REJECTED
            awaitingFinishConfirmation == true -> Kind.FINISH_CONFIRM
            question != null -> Kind.NEXT_QUESTION
            else -> Kind.UNKNOWN
        }
}
