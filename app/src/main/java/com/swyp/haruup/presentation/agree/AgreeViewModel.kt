package com.swyp.haruup.presentation.agree

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** 약관 항목. iOS 의 term1 / term2 / term3 에 대응합니다. */
enum class TermType(val label: String, val url: String?) {
    SERVICE(
        label = "[필수] 서비스 이용약관에 동의합니다",
        url = "https://melodic-roar-3e1.notion.site/2e0849f596f380eabc6de523ab0d9bd9",
    ),
    PRIVACY(
        label = "[필수] 개인정보 수집 및 이용에 동의합니다",
        url = "https://melodic-roar-3e1.notion.site/2e0849f596f380969043ee98e361c7bf",
    ),

    /** 원문 링크가 없어 화살표를 표시하지 않습니다. */
    AGE(
        label = "[필수] 만 14세 이상입니다",
        url = null,
    ),
}

data class AgreeUiState(
    val checked: Set<TermType> = emptySet(),
) {
    val isAllChecked: Boolean = checked.size == TermType.entries.size

    /** 세 항목 모두 필수라 전체 동의와 조건이 같습니다. */
    val isConfirmEnabled: Boolean = isAllChecked

    fun isChecked(term: TermType): Boolean = term in checked
}

/**
 * iOS 의 AgreeViewModel 에 대응합니다.
 */
@HiltViewModel
class AgreeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AgreeUiState())
    val uiState: StateFlow<AgreeUiState> = _uiState.asStateFlow()

    fun onTermClick(term: TermType) {
        _uiState.update { state ->
            val next = state.checked.toMutableSet()
            if (!next.add(term)) next.remove(term)
            state.copy(checked = next)
        }
    }

    /** 전체 동의는 토글입니다. 하나라도 빠져 있으면 전부 체크하고, 모두 체크돼 있으면 전부 해제합니다. */
    fun onAllClick() {
        _uiState.update { state ->
            state.copy(
                checked = if (state.isAllChecked) emptySet() else TermType.entries.toSet(),
            )
        }
    }
}
