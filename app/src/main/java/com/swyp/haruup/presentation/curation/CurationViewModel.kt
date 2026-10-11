package com.swyp.haruup.presentation.curation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.model.ChatbotMission
import com.swyp.haruup.data.model.ChatbotSetupRequest
import com.swyp.haruup.data.model.CurationData
import com.swyp.haruup.network.service.ChatbotService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 큐레이션 플로우 전체가 공유하는 데이터 홀더입니다.
 * iOS 에서 Coordinator 가 CurationData 를 들고 단계마다 넘겨주던 역할을 대신합니다.
 *
 * 이 ViewModel 은 큐레이션 중첩 그래프(Route.CURATION_GRAPH)의 BackStackEntry 에 스코프됩니다.
 * 즉 큐레이션에 진입할 때 만들어지고 플로우를 벗어나면 자동으로 정리되므로,
 * 다시 들어왔을 때 이전 입력이 남아 있지 않습니다.
 *
 * 각 단계 화면은 자기 화면의 상태만 담당하는 별도 ViewModel 을 쓰고,
 * 다음 단계로 넘길 값만 여기에 씁니다.
 */
@HiltViewModel
class CurationViewModel @Inject constructor(
    private val chatbotService: ChatbotService,
) : ViewModel() {

    private val _curationData = MutableStateFlow(CurationData())
    val curationData: StateFlow<CurationData> = _curationData.asStateFlow()

    /** ① 캐릭터 선택 */
    fun setCharacterId(characterId: Int) {
        _curationData.update { it.copy(characterId = characterId) }
    }

    /** ③ 성격 선택 */
    fun setPersonality(code: String) {
        _curationData.update { it.copy(personality = code) }
    }

    /** 서버에 캐릭터와 닉네임을 저장하는 중인지. 완료가 두 번 눌려도 한 번만 보냅니다. */
    private var isSettingUp = false

    /**
     * ④ 큐레이션 챗봇 완료.
     *
     * 고른 캐릭터와 챗봇에서 받은 닉네임을 서버에 저장한 뒤 미션 선택으로 넘어갑니다.
     * (iOS AppCoordinator.showCurationFlow 의 chatbotSetup 호출에 대응)
     *
     * 저장에 실패해도 넘어갑니다. 서버가 같은 요청을 여러 번 받아도 괜찮게 처리하고,
     * 여기서 막으면 챗봇 대화를 처음부터 다시 해야 하기 때문입니다. (iOS 와 같은 처리)
     */
    fun completeChat(nickname: String, missions: List<ChatbotMission>, onDone: () -> Unit) {
        if (isSettingUp) return
        _curationData.update { it.copy(nickname = nickname, chatbotMissions = missions) }

        // 캐릭터 선택을 거치지 않았다면 보낼 것이 없습니다. (iOS 도 건너뜁니다)
        val characterId = _curationData.value.characterId
        if (characterId == null || nickname.isBlank()) {
            onDone()
            return
        }

        isSettingUp = true
        viewModelScope.launch {
            runCatching { chatbotService.chatbotSetup(ChatbotSetupRequest(characterId, nickname)) }
            isSettingUp = false
            onDone()
        }
    }
}
