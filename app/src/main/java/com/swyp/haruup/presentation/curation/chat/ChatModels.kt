package com.swyp.haruup.presentation.curation.chat

import java.util.UUID

/** iOS 의 ChatMessageType 에 대응합니다. */
enum class ChatMessageType { BOT, USER }

/**
 * 대화 한 줄. iOS 의 ChatMessage 에 대응합니다.
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val type: ChatMessageType,
    val text: String,
    /** 본문 안에서 강조할 부분 문자열 */
    val highlightedText: String? = null,
    /** 봇 메시지에만 붙습니다. 말풍선 아래에 칩 줄로 그려집니다. */
    val suggestions: List<String> = emptyList(),
    /** 말풍선 아래에 작게 붙는 보조 문구 */
    val subtitleText: String? = null,
    /** 응답을 기다리는 중이면 글자에 shimmer 를 입힙니다. */
    val isShimmering: Boolean = false,
    /** 사용자가 고쳐서 다시 입력해야 하는 안내인지 (목표를 하나만 입력해달라는 경우 등) */
    val isError: Boolean = false,
)

/**
 * 목록에 그려지는 항목. iOS 의 ChatDisplayItem 에 대응합니다.
 */
sealed interface ChatDisplayItem {
    val key: String

    data class Bot(val message: ChatMessage) : ChatDisplayItem {
        override val key: String = message.id
    }

    data class User(val message: ChatMessage) : ChatDisplayItem {
        override val key: String = message.id
    }

    /** 사용자가 골라서 바로 답할 수 있는 예시 칩 */
    data class SuggestionChips(val suggestions: List<String>) : ChatDisplayItem {
        override val key: String = "chips_${suggestions.hashCode()}"
    }
}
