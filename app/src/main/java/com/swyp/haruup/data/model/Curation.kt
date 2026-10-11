package com.swyp.haruup.data.model

import kotlinx.serialization.Serializable

/**
 * 큐레이션 진행 중 단계별로 쌓이는 데이터입니다.
 * iOS 의 CurationData 에 대응하지만, 챗봇 플로우에서 실제로 쓰이는 값만 남겼습니다.
 *
 * iOS CurationData 에는 job / gender / birthDate / interest 등도 있으나
 * AppCoordinator 주석대로 챗봇 플로우에서는 캐릭터 ID 와 닉네임만 서버에 등록하므로
 * 나머지는 구 9단계 플로우의 잔재로 보고 옮기지 않았습니다.
 */
data class CurationData(
    /** ① 캐릭터 선택 단계에서 설정 */
    val characterId: Int? = null,

    /**
     * ③ 성격 선택 단계에서 설정.
     * iOS 는 서버에 바로 보내고 보관하지 않지만, 뒤로 가기 시 선택 상태를 복원하려고 들고 있습니다.
     */
    val personality: String? = null,

    /** ④ 큐레이션 챗봇 완료 시 설정 */
    val nickname: String? = null,

    /** ④ 큐레이션 챗봇 완료 시 설정. 오늘의 미션 선택 화면으로 전달합니다. */
    val chatbotMissions: List<ChatbotMission> = emptyList(),
) {
    /**
     * chatbotSetup 을 호출할 수 있는 상태인지 확인합니다.
     * iOS AppCoordinator 가 characterId 와 nickname 을 guard 로 검사하는 것과 같은 조건입니다.
     */
    val isReadyForSetup: Boolean = characterId != null && !nickname.isNullOrBlank()
}

/** 챗봇이 생성한 미션. iOS 의 ChatbotMissionDto 에 대응합니다. */
@Serializable
data class ChatbotMission(
    val id: Int,
    val missionContent: String,
    val missionDescription: String? = null,
    /** 1=하, 2=중, 3=상 */
    val difficulty: Int,
    val expEarned: Int,
)

/** POST /api/member/curation/chatbot-setup 요청 body */
@Serializable
data class ChatbotSetupRequest(
    val characterId: Int,
    val nickname: String,
)
