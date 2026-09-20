package com.swyp.haruup.presentation.curation.personality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.model.PersonalityData
import com.swyp.haruup.data.model.SelectPersonalityRequest
import com.swyp.haruup.network.service.CharacterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PersonalitySelectUiState(
    val personalities: List<PersonalityData> = emptyList(),
    val selectedIndex: Int? = null,
    val isLoading: Boolean = false,
) {
    val isNextEnabled: Boolean = selectedIndex != null

    val selectedCode: String? = selectedIndex?.let { personalities.getOrNull(it)?.code }
}

/**
 * 큐레이션 3단계. iOS 의 PersonalitySelectViewModel 에 대응합니다.
 * 여기서 고른 성격은 큐레이션 꼬리질문의 말투에만 반영됩니다.
 */
@HiltViewModel
class PersonalitySelectViewModel @Inject constructor(
    private val characterService: CharacterService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalitySelectUiState())
    val uiState: StateFlow<PersonalitySelectUiState> = _uiState.asStateFlow()

    init {
        loadPersonalities()
    }

    private fun loadPersonalities() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val list = runCatching { characterService.personalityList() }
                .getOrNull()
                ?.data
                ?.takeIf { it.isNotEmpty() }
                ?: FALLBACK_PERSONALITIES

            _uiState.update { it.copy(personalities = list, isLoading = false) }
        }
    }

    fun onPersonalitySelected(index: Int) {
        _uiState.update { it.copy(selectedIndex = index) }
    }

    /**
     * 고른 성격을 서버에 저장하고 [onComplete] 를 호출합니다.
     *
     * 저장에 실패해도 큐레이션은 그대로 진행합니다.
     * 서버가 성격을 고르지 않은 회원에게 기본값을 적용하므로 대화가 막히지 않습니다. (iOS 와 같은 정책)
     */
    fun onNextClick(onComplete: (code: String) -> Unit) {
        val code = _uiState.value.selectedCode ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { characterService.selectPersonality(SelectPersonalityRequest(code)) }
            _uiState.update { it.copy(isLoading = false) }
            onComplete(code)
        }
    }

    companion object {
        /**
         * 서버 목록을 못 받았을 때 쓰는 기본 선택지입니다.
         * 성격을 못 고르면 큐레이션 자체를 진행할 수 없으므로 화면을 비워 두지 않습니다.
         * 서버가 미선택 회원에게 적용하는 기본값과 같은 순서입니다.
         */
        val FALLBACK_PERSONALITIES = listOf(
            PersonalityData(code = "WARM_FRIEND", label = "따뜻하게 응원하며 함께 가는 친구"),
            PersonalityData(code = "CLEAR_COACH", label = "명확한 계획으로 이끌어주는 코치"),
        )
    }
}
