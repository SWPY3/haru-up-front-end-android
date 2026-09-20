package com.swyp.haruup.presentation.curation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.core.util.NicknameValidator
import com.swyp.haruup.data.model.ChatbotAnswerData
import com.swyp.haruup.data.model.ChatbotAnswerRequest
import com.swyp.haruup.data.model.ChatbotMission
import com.swyp.haruup.data.model.NicknameDuplicateRequest
import com.swyp.haruup.network.service.ChatbotService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CurationChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    /** 서버가 첫 질문에 내려주는 안내 문구로 바뀝니다. */
    val inputPlaceholder: String = DEFAULT_PLACEHOLDER,
    val isLoading: Boolean = false,
    /** 0.0 ~ 1.0. 질문 개수가 대화마다 달라져 단계 수로는 표현할 수 없습니다. */
    val progress: Float = 1f / BASELINE_TOTAL_STEPS,
    /** 대화가 끝나고 미션이 만들어진 상태 */
    val isCompleted: Boolean = false,
) {
    /**
     * 봇 메시지에 예시가 붙어 있으면 말풍선 다음에 칩 줄을 하나 더 그립니다.
     * iOS 가 messages → displayItems 로 펼치던 것과 같습니다.
     */
    val displayItems: List<ChatDisplayItem> = buildList {
        messages.forEach { message ->
            when (message.type) {
                ChatMessageType.BOT -> {
                    add(ChatDisplayItem.Bot(message))
                    if (message.suggestions.isNotEmpty()) {
                        add(ChatDisplayItem.SuggestionChips(message.suggestions))
                    }
                }

                ChatMessageType.USER -> add(ChatDisplayItem.User(message))
            }
        }
    }

    val canSend: Boolean = inputText.isNotBlank() && !isLoading

    companion object {
        const val DEFAULT_PLACEHOLDER = "답변을 입력해주세요"

        /**
         * 진행률 계산에 쓰는 기준 단계 수 (닉네임 1 + 목표 1 + 꼬리질문 5).
         * 서버가 꼬리질문을 3~8개 사이에서 조절하므로 실제 총 개수는 대화가 끝나야 압니다.
         */
        const val BASELINE_TOTAL_STEPS = 7
    }
}

/** 대화 단계. 닉네임을 받은 뒤에야 챗봇 세션을 시작합니다. */
private enum class ChatPhase { NICKNAME, CHATBOT }

/**
 * 큐레이션 4단계 챗봇. iOS 의 CurationChatViewModel 에 대응합니다.
 */
@HiltViewModel
class CurationChatViewModel @Inject constructor(
    private val chatbotService: ChatbotService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurationChatUiState())
    val uiState: StateFlow<CurationChatUiState> = _uiState.asStateFlow()

    private var phase = ChatPhase.NICKNAME
    private var sessionId: String? = null
    private var isLastQuestion = false

    /**
     * 마무리 확인("이대로 마무리할까요?")을 띄워 두고 사용자의 예/아니오를 기다리는 중인지.
     * 이 상태에서 보낸 답변은 꼬리질문 답변이 아니라 마무리 여부 선택입니다.
     */
    private var awaitingFinishConfirmation = false

    var collectedNickname: String = ""
        private set

    var completedMissions: List<ChatbotMission> = emptyList()
        private set

    /** 화면에 처음 들어왔을 때 한 번만 호출합니다. */
    fun onStart() {
        if (_uiState.value.messages.isNotEmpty()) return
        appendBot("닉네임을 입력해주세요.\n하루업에서 불리고 싶은 이름을 적어주세요.")
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendClick() {
        val answer = _uiState.value.inputText.trim()
        if (answer.isBlank() || _uiState.value.isLoading) return
        _uiState.update { it.copy(inputText = "") }
        handleUserAnswer(answer)
    }

    /**
     * 예시 칩을 누르면 입력창에 문구를 채웁니다. 바로 보내지 않습니다.
     * 사용자가 그대로 보낼지 고쳐 쓸지 고를 수 있어야 하기 때문입니다. (iOS prefillText 와 동일)
     */
    fun onSuggestionClick(suggestion: String) {
        _uiState.update { it.copy(inputText = suggestion) }
    }

    // MARK: - 답변 처리

    private fun handleUserAnswer(answer: String) {
        when (phase) {
            ChatPhase.NICKNAME -> handleNicknameInput(answer)
            ChatPhase.CHATBOT -> submitAnswer(answer)
        }
    }

    private fun handleNicknameInput(nickname: String) {
        appendUser(nickname)

        NicknameValidator.validate(nickname)?.let { errorMessage ->
            appendBot(errorMessage)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = runCatching {
                chatbotService.checkNicknameDuplicate(NicknameDuplicateRequest(nickname))
            }

            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { response ->
                    if (response.success) {
                        collectedNickname = nickname
                        appendBot("${nickname}님, 반갑습니다! 🎉\n이제 목표를 설정해볼게요.")
                        phase = ChatPhase.CHATBOT
                        startChatbot()
                    } else {
                        appendBot("이미 사용 중인 닉네임이에요.\n다른 닉네임을 입력해주세요.")
                    }
                },
                onFailure = {
                    appendBot("닉네임 확인 중 오류가 발생했어요.\n다시 시도해주세요.")
                },
            )
        }
    }

    private fun startChatbot() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = runCatching { chatbotService.start() }

            _uiState.update { it.copy(isLoading = false) }

            val data = result.getOrNull()?.data
            if (data == null) {
                appendBot("연결에 실패했어요. 다시 시도해주세요.")
                return@launch
            }

            sessionId = data.sessionId
            updateProgress(data.questionNumber)

            // 첫 질문에는 선택지가 없습니다. 사용자가 예시를 그대로 고르면
            // 목표가 구체화되지 않아 서버가 placeholder 로만 예시를 내려줍니다.
            data.placeholder?.takeIf { it.isNotEmpty() }?.let { placeholder ->
                _uiState.update { it.copy(inputPlaceholder = placeholder) }
            }

            appendBot(data.question, suggestions = data.examples)
        }
    }

    private fun submitAnswer(answer: String) {
        val currentSessionId = sessionId ?: return

        appendUser(answer)

        // 미션 생성으로 이어질 답변이면 생성 중 메시지를 먼저 보여줍니다.
        // 마무리를 골라도 시간 투자가 필요한 목표면 질문이 한 번 더 오므로,
        // 완료가 아닌 응답이 오면 이 메시지를 지웁니다. (removePendingShimmer)
        if (isLastQuestion || (awaitingFinishConfirmation && isAffirmative(answer))) {
            appendBot("${collectedNickname}님을 위한 맞춤 미션을 만드는 중이에요!", isShimmering = true)
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = runCatching {
                chatbotService.answer(ChatbotAnswerRequest(currentSessionId, answer))
            }

            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { response -> handleAnswerResponse(response.data) },
                onFailure = {
                    removePendingShimmer()
                    appendBot("오류가 발생했어요. 다시 시도해주세요.")
                },
            )
        }
    }

    private fun handleAnswerResponse(data: ChatbotAnswerData?) {
        if (data == null) {
            removePendingShimmer()
            appendBot("예상하지 못한 응답을 받았어요. 다시 시도해주세요.")
            return
        }

        when (data.kind) {
            ChatbotAnswerData.Kind.COMPLETED -> handleCompleted(data)
            ChatbotAnswerData.Kind.GOAL_REJECTED -> handleGoalRejected(data)
            ChatbotAnswerData.Kind.FINISH_CONFIRM -> handleFinishConfirm(data)
            ChatbotAnswerData.Kind.NEXT_QUESTION -> handleNextQuestion(data)
            ChatbotAnswerData.Kind.UNKNOWN -> {
                // 앱이 모르는 응답이 와도 대화가 멈춘 것처럼 보이지 않게 안내합니다.
                removePendingShimmer()
                appendBot("예상하지 못한 응답을 받았어요. 다시 시도해주세요.")
            }
        }
    }

    /** 대화 종료 — 미션 생성 완료 */
    private fun handleCompleted(data: ChatbotAnswerData) {
        awaitingFinishConfirmation = false
        completedMissions = data.missions.orEmpty()
        appendBot("좋아요! 답변을 바탕으로 맞춤 미션을 준비했어요!!!🎉")
        _uiState.update { it.copy(progress = 1f, isCompleted = true) }
    }

    /**
     * 목표를 2개 이상 입력한 경우 — 질문을 진행하지 않고 다시 입력받습니다.
     * 세션은 그대로라 같은 sessionId 로 목표만 다시 보내면 됩니다.
     */
    private fun handleGoalRejected(data: ChatbotAnswerData) {
        removePendingShimmer()

        val message = data.message ?: "목표를 하나만 입력해주세요!"
        val detected = data.detectedGoals.orEmpty()

        appendBot(
            text = message,
            highlightedText = message,
            subtitleText = detected.takeIf { it.isNotEmpty() }
                ?.let { "입력하신 목표: ${it.joinToString(", ")}" },
            isError = true,
        )
    }

    /** 정보가 충분히 모였을 때 — 요약을 보여주고 마무리할지 묻습니다. */
    private fun handleFinishConfirm(data: ChatbotAnswerData) {
        removePendingShimmer()
        awaitingFinishConfirmation = true
        isLastQuestion = false

        data.summary?.takeIf { it.isNotEmpty() }?.let { appendBot(it) }

        appendBot(
            text = data.question ?: "이대로 마무리할까요?",
            suggestions = data.examples.orEmpty(),
        )
    }

    /** 다음 질문 표시 (AI 꼬리질문 또는 투자 가능 시간 고정 질문) */
    private fun handleNextQuestion(data: ChatbotAnswerData) {
        removePendingShimmer()
        val question = data.question ?: return

        awaitingFinishConfirmation = false
        isLastQuestion = data.isLast ?: false

        data.questionNumber?.let { updateProgress(it) }

        appendBot(
            text = question,
            suggestions = data.examples.orEmpty(),
            subtitleText = if (isLastQuestion) "마지막 질문이에요!" else null,
        )
    }

    // MARK: - 보조

    /**
     * 대화 단계에 맞춰 진행률을 갱신합니다.
     *
     * 꼬리질문 개수가 대화마다 달라 총 단계 수를 미리 알 수 없습니다.
     * 그래서 기준값을 쓰되, 대화가 그보다 길어지면 분모를 함께 늘려
     * 진행률이 뒤로 가거나 100% 에 먼저 도달하는 일이 없게 합니다.
     */
    private fun updateProgress(questionNumber: Int) {
        val currentStep = questionNumber + 1 // 닉네임 단계를 한 칸으로 칩니다
        val total = maxOf(currentStep + 1, CurationChatUiState.BASELINE_TOTAL_STEPS)
        val ratio = currentStep.toFloat() / total
        _uiState.update { it.copy(progress = minOf(ratio, MAX_PROGRESS_BEFORE_COMPLETE)) }
    }

    /**
     * 미션 생성 중 메시지를 걷어냅니다.
     *
     * 마무리를 골라도 시간 투자가 필요한 목표면 질문이 한 번 더 옵니다.
     * 그때 "만드는 중" 문구가 남아 있으면 사용자가 끝난 줄 알았다가 질문을 다시 받게 됩니다.
     */
    private fun removePendingShimmer() {
        _uiState.update { state ->
            if (state.messages.lastOrNull()?.isShimmering == true) {
                state.copy(messages = state.messages.dropLast(1))
            } else {
                state
            }
        }
    }

    private fun appendBot(
        text: String,
        suggestions: List<String> = emptyList(),
        highlightedText: String? = null,
        subtitleText: String? = null,
        isShimmering: Boolean = false,
        isError: Boolean = false,
    ) {
        append(
            ChatMessage(
                type = ChatMessageType.BOT,
                text = text,
                suggestions = suggestions,
                highlightedText = highlightedText,
                subtitleText = subtitleText,
                isShimmering = isShimmering,
                isError = isError,
            )
        )
    }

    private fun appendUser(text: String) {
        append(ChatMessage(type = ChatMessageType.USER, text = text))
    }

    private fun append(message: ChatMessage) {
        _uiState.update { it.copy(messages = it.messages + message) }
    }

    companion object {
        /** 완료 전에는 진행률을 이 값 이상으로 올리지 않습니다. 다 찼는데 질문이 더 나오는 상황을 막습니다. */
        private const val MAX_PROGRESS_BEFORE_COMPLETE = 0.95f

        /**
         * 마무리 확인 답변이 긍정인지 판단합니다.
         *
         * 판정 자체는 서버가 하고, 앱은 "미션 생성 중" 표시를 미리 띄울지 정하는 데만 씁니다.
         * 그래서 틀려도 화면 연출만 달라지고 대화 흐름에는 영향이 없습니다.
         * 부정을 먼저 걸러야 "아니요, 미션 만들어주세요" 같은 답을 긍정으로 잘못 읽지 않습니다.
         */
        fun isAffirmative(answer: String): Boolean {
            val negatives = listOf("아니", "아뇨", "싫", "더 ", "계속", "나중")
            if (negatives.any { answer.contains(it) }) return false

            val affirmatives = listOf("네", "예", "응", "좋", "만들", "마무리", "끝", "그만", "충분", "시작")
            return affirmatives.any { answer.contains(it) }
        }
    }
}
