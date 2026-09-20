package com.swyp.haruup.presentation.curation

import androidx.lifecycle.ViewModel
import com.swyp.haruup.data.model.ChatbotMission
import com.swyp.haruup.data.model.CurationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
class CurationViewModel @Inject constructor() : ViewModel() {

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

    /** ④ 큐레이션 챗봇 완료 */
    fun setChatResult(nickname: String, missions: List<ChatbotMission>) {
        _curationData.update { it.copy(nickname = nickname, chatbotMissions = missions) }
    }
}
