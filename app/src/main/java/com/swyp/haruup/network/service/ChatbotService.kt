package com.swyp.haruup.network.service

import com.swyp.haruup.data.model.ChatbotAnswerData
import com.swyp.haruup.data.model.ChatbotAnswerRequest
import com.swyp.haruup.data.model.ChatbotSetupRequest
import com.swyp.haruup.data.model.ChatbotStartData
import com.swyp.haruup.data.model.NicknameDuplicateRequest
import com.swyp.haruup.network.ApiPath
import com.swyp.haruup.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * iOS 의 ChatbotService 에 대응합니다.
 */
interface ChatbotService {

    /** 챗봇 시작 — sessionId 와 첫 질문을 받습니다. */
    @POST(ApiPath.Chatbot.START)
    suspend fun start(): ApiResponse<ChatbotStartData>

    /** 사용자 답변 제출 — 다음 질문 또는 완료를 받습니다. */
    @POST(ApiPath.Chatbot.ANSWER)
    suspend fun answer(@Body request: ChatbotAnswerRequest): ApiResponse<ChatbotAnswerData>

    /** 챗봇 완료 후 캐릭터와 닉네임을 저장합니다. */
    @POST(ApiPath.Chatbot.SETUP)
    suspend fun chatbotSetup(@Body request: ChatbotSetupRequest): ApiResponse<String>

    /**
     * 닉네임 중복 확인.
     * success 가 true 면 사용 가능, false 면 이미 쓰는 닉네임입니다.
     */
    @POST(ApiPath.Profile.NICKNAME_DUPLICATE_CHECK)
    suspend fun checkNicknameDuplicate(
        @Body request: NicknameDuplicateRequest,
    ): ApiResponse<Boolean>
}
