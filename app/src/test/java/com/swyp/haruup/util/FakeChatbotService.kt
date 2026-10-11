package com.swyp.haruup.util

import com.swyp.haruup.data.model.ChatbotAnswerData
import com.swyp.haruup.data.model.ChatbotAnswerRequest
import com.swyp.haruup.data.model.ChatbotSetupRequest
import com.swyp.haruup.data.model.ChatbotStartData
import com.swyp.haruup.data.model.NicknameDuplicateRequest
import com.swyp.haruup.network.ApiResponse
import com.swyp.haruup.network.service.ChatbotService

/**
 * 테스트용 ChatbotService 입니다. 지금은 캐릭터·닉네임 저장만 확인합니다.
 * 대화 API 는 쓰는 테스트가 생기면 채웁니다.
 */
class FakeChatbotService(
    var shouldFail: Boolean = false,
) : ChatbotService {

    val setupRequests = mutableListOf<ChatbotSetupRequest>()

    override suspend fun start(): ApiResponse<ChatbotStartData> = error("쓰지 않는 API 입니다.")

    override suspend fun answer(request: ChatbotAnswerRequest): ApiResponse<ChatbotAnswerData> =
        error("쓰지 않는 API 입니다.")

    override suspend fun chatbotSetup(request: ChatbotSetupRequest): ApiResponse<String> {
        setupRequests += request
        if (shouldFail) error("저장하지 못했습니다.")
        return ApiResponse(success = true, data = "ok")
    }

    override suspend fun checkNicknameDuplicate(request: NicknameDuplicateRequest): ApiResponse<Boolean> =
        error("쓰지 않는 API 입니다.")
}
