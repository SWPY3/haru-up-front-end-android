package com.swyp.haruup.presentation.curation.character

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.R
import com.swyp.haruup.network.service.CharacterService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 선택 가능한 캐릭터.
 * 이미지는 앱에 번들된 에셋이고, 이름만 서버에서 받아 덮어씁니다. (iOS 와 동일)
 * id 는 서버의 characterId 로, 1부터 시작합니다.
 */
enum class CharacterMate(
    val id: Int,
    val defaultName: String,
    @DrawableRes val imageRes: Int,
) {
    HARU(id = 1, defaultName = "하루", imageRes = R.drawable.character_haru_level1),
    NARU(id = 2, defaultName = "나루", imageRes = R.drawable.character_naru_level1),
}

data class CharacterSelectUiState(
    val selected: CharacterMate = CharacterMate.HARU,
    /** characterId → 서버가 내려준 이름 */
    val serverNames: Map<Int, String> = emptyMap(),
) {
    val mates: List<CharacterMate> = CharacterMate.entries

    /** 서버 이름이 있으면 그것을 쓰고, 없으면 앱에 있는 기본 이름을 씁니다. */
    val displayName: String = serverNames[selected.id] ?: selected.defaultName

    val canGoPrevious: Boolean = selected.ordinal > 0
    val canGoNext: Boolean = selected.ordinal < mates.lastIndex
}

@HiltViewModel
class CharacterSelectViewModel @Inject constructor(
    private val characterService: CharacterService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterSelectUiState())
    val uiState: StateFlow<CharacterSelectUiState> = _uiState.asStateFlow()

    init {
        loadCharacterNames()
    }

    /**
     * 캐릭터 이름을 서버에서 받아옵니다.
     * 실패하면 기본 이름을 그대로 쓰고 넘어갑니다.
     * 이름을 못 받았다고 캐릭터 선택 자체를 막을 이유는 없습니다. (iOS 와 동일한 정책)
     */
    private fun loadCharacterNames() {
        viewModelScope.launch {
            runCatching { characterService.characterList() }
                .onSuccess { characters ->
                    val names = characters
                        .filter { !it.name.isNullOrEmpty() }
                        .associate { it.id to it.name!! }
                    _uiState.update { it.copy(serverNames = names) }
                }
        }
    }

    fun onPreviousClick() {
        _uiState.update { state ->
            val index = state.selected.ordinal
            if (index > 0) state.copy(selected = state.mates[index - 1]) else state
        }
    }

    fun onNextClick() {
        _uiState.update { state ->
            val index = state.selected.ordinal
            if (index < state.mates.lastIndex) state.copy(selected = state.mates[index + 1]) else state
        }
    }

    /** 캐릭터 이미지를 탭하면 다음 캐릭터로 넘어가고, 마지막이면 처음으로 돌아옵니다. */
    fun onCharacterClick() {
        _uiState.update { state ->
            val next = (state.selected.ordinal + 1) % state.mates.size
            state.copy(selected = state.mates[next])
        }
    }
}
