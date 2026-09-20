package com.swyp.haruup.presentation.curation.character

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 한 화면 안에서 말풍선만 바꿔가며 두 번 보여줍니다.
 * iOS 의 CharacterSelectCompleteViewModel.Step 과 같습니다.
 */
enum class CompleteStep {
    /** 캐릭터별 인사 말풍선. 버튼은 "다음" */
    WELCOME,

    /** 공통 안내 말풍선. 버튼은 "시작하기" */
    GUIDE,
}

@HiltViewModel
class CharacterCompleteViewModel @Inject constructor() : ViewModel() {

    private val _step = MutableStateFlow(CompleteStep.WELCOME)
    val step: StateFlow<CompleteStep> = _step.asStateFlow()

    /** 마지막 단계에서 눌렀는지 여부를 돌려줍니다. true 면 다음 화면으로 넘어갑니다. */
    fun onNextClick(): Boolean =
        if (_step.value == CompleteStep.WELCOME) {
            _step.value = CompleteStep.GUIDE
            false
        } else {
            true
        }
}
