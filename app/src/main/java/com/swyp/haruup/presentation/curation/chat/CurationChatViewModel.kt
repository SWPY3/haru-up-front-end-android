package com.swyp.haruup.presentation.curation.chat

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CurationChatUiState(
    val displayItems: List<ChatDisplayItem> = emptyList(),
    val inputText: String = "",
    /** 서버가 첫 질문에 내려주는 안내 문구로 바뀝니다. */
    val inputPlaceholder: String = DEFAULT_PLACEHOLDER,
    val isLoading: Boolean = false,
    /** 0.0 ~ 1.0. 질문 개수가 대화마다 달라져 단계 수로는 표현할 수 없습니다. */
    val progress: Float = 0.15f,
) {
    val canSend: Boolean = inputText.isNotBlank() && !isLoading

    companion object {
        const val DEFAULT_PLACEHOLDER = "답변을 입력해주세요"
    }
}

/**
 * 큐레이션 4단계 챗봇. iOS 의 CurationChatViewModel 에 대응합니다.
 *
 * 현재는 대화 UI 를 확인하기 위한 로컬 동작만 들어 있습니다.
 * 세션 관리와 chatbot/start·answer 연동은 다음 단계에서 붙입니다.
 */
@HiltViewModel
class CurationChatViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CurationChatUiState())
    val uiState: StateFlow<CurationChatUiState> = _uiState.asStateFlow()

    /** 화면에 처음 들어왔을 때 한 번만 호출합니다. */
    fun onStart() {
        if (_uiState.value.displayItems.isNotEmpty()) return

        // TODO: chatbot/start 응답으로 교체
        _uiState.update {
            it.copy(
                displayItems = listOf(
                    ChatDisplayItem.Bot(
                        ChatMessage(
                            type = ChatMessageType.BOT,
                            text = "안녕하세요! 어떤 목표를 이루고 싶으신가요?",
                        )
                    ),
                ),
            )
        }
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendClick() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        sendAnswer(text)
    }

    fun onSuggestionClick(suggestion: String) {
        sendAnswer(suggestion)
    }

    /**
     * 사용자 답변을 목록에 붙입니다.
     * TODO: chatbot/answer 를 호출하고 응답 종류에 따라 분기하도록 교체
     */
    private fun sendAnswer(text: String) {
        _uiState.update { state ->
            val withoutChips = state.displayItems.filterNot { it is ChatDisplayItem.SuggestionChips }

            state.copy(
                displayItems = withoutChips + ChatDisplayItem.User(
                    ChatMessage(type = ChatMessageType.USER, text = text)
                ),
                inputText = "",
            )
        }
    }
}
