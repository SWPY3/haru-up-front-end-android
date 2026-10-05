package com.swyp.haruup.presentation.mypage.interest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.data.model.UpdateMemberInterestRequest
import com.swyp.haruup.network.service.InterestService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InterestEditUiState(
    val interests: List<InterestOption> = emptyList(),
    val details: List<InterestOption> = emptyList(),
    val goals: List<InterestOption> = emptyList(),

    val selectedInterest: InterestOption? = null,
    val selectedDetail: InterestOption? = null,
    val selectedGoal: InterestOption? = null,

    /** "기타" 를 골라 직접 적은 세부 관심사 이름 */
    val customDetailName: String? = null,
    /** "직접 입력" 을 골라 직접 적은 목표 이름 */
    val customGoalName: String? = null,

    /** 서버에 저장되어 있는 경로. 이 값과 달라야 완료 버튼이 켜집니다. */
    val savedPath: List<String> = emptyList(),

    val expanded: InterestDropdown? = null,
    val directInputTarget: DirectInputTarget? = null,
    val isCancelDialogVisible: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null,
) {
    /** 세부 관심사 칸에 보여 줄 이름. 직접 적은 값이 있으면 그쪽이 먼저입니다. */
    val detailDisplayName: String? = customDetailName?.takeIf { it.isNotBlank() } ?: selectedDetail?.displayName

    /**
     * 목표 칸에 보여 줄 이름입니다. iOS 의 currentGoalName 순서를 그대로 따릅니다.
     * 세부 관심사를 고르기 전에는 비워 두고, 목표가 아예 없는 분류면 그렇게 알려 줍니다.
     */
    val goalDisplayName: String? = when {
        selectedDetail == null -> null
        !customGoalName.isNullOrBlank() -> customGoalName
        selectedGoal != null -> selectedGoal.displayName
        goals.isEmpty() -> NO_GOAL
        else -> null
    }

    /** 세부 관심사를 고르기 전이거나 고를 목표가 있을 때만 누를 수 있습니다. */
    val isGoalEnabled: Boolean =
        selectedDetail == null || selectedGoal != null || goals.isNotEmpty()

    /** 지금 화면에 떠 있는 경로입니다. 길이가 3이 되어야 저장할 수 있습니다. */
    val currentPath: List<String> = buildList {
        selectedInterest?.let { add(it.displayName) }
        if (selectedDetail != null) add(detailDisplayName.orEmpty())
        // 목표가 없는 분류는 마지막 칸을 빈 문자열로 채웁니다. (iOS 와 동일)
        if (selectedDetail != null && goals.isEmpty()) {
            add("")
        } else if (selectedGoal != null) {
            add(customGoalName?.takeIf { it.isNotBlank() } ?: selectedGoal.displayName)
        }
    }

    val isModified: Boolean = currentPath != savedPath

    val isCompleteEnabled: Boolean = when {
        isSubmitting -> false
        selectedInterest == null || selectedDetail == null -> false
        // 직접 입력을 골랐는데 아직 안 적었으면 저장할 수 없습니다.
        selectedGoal?.isDirectInput() == true && customGoalName.isNullOrBlank() -> false
        currentPath.size != PATH_SIZE -> false
        else -> isModified
    }

    companion object {
        const val NO_GOAL = "선택할 목표 없음"

        /** 서버가 받는 경로는 항상 [관심사, 세부 관심사, 목표] 세 칸입니다. */
        const val PATH_SIZE = 3
    }
}

/**
 * 마이페이지 > 관심사 수정. iOS 의 InterestEditViewModel 에 대응합니다.
 *
 * 저장된 값은 이름만 남아 있어서 들어올 때 이름으로 목록을 뒤져 id 를 되찾습니다.
 * 그래야 하위 목록을 불러올 수 있기 때문입니다.
 */
@HiltViewModel
class InterestEditViewModel @Inject constructor(
    private val interestService: InterestService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterestEditUiState())
    val uiState: StateFlow<InterestEditUiState> = _uiState.asStateFlow()

    /** 수정 대상 식별자. 저장할 때 경로에 붙습니다. */
    private var memberInterestId: Int? = null

    init {
        restoreSavedInterest()
    }

    /**
     * 저장된 경로를 화면에 되살립니다.
     *
     * 이름을 먼저 띄우고 그 다음에 id 를 찾는 iOS 방식 대신,
     * 한 번에 세 단계를 모두 맞춘 뒤 화면에 올립니다. 중간 상태가 잠깐 비치지 않습니다.
     */
    private fun restoreSavedInterest() {
        viewModelScope.launch {
            val saved = runCatching { interestService.memberInterests() }
                .getOrNull()
                ?.interests
                ?.firstOrNull()

            val interests = fetchOptions(parentId = null)
            _uiState.update { it.copy(interests = interests) }

            if (saved == null) return@launch
            memberInterestId = saved.memberInterestId

            val path = saved.directFullPath
            val interest = interests.firstOrNull { it.displayName == path.getOrNull(0) } ?: return@launch

            val details = fetchOptions(parentId = interest.id)
            val (detail, customDetail) = matchOrDirectInput(details, path.getOrNull(1))

            val goals = detail?.let { fetchOptions(parentId = it.id) }.orEmpty()
            // 목표 칸이 빈 문자열이면 목표가 없는 분류입니다.
            val goalName = path.getOrNull(2)?.takeIf { it.isNotBlank() }
            val (goal, customGoal) = matchOrDirectInput(goals, goalName)

            _uiState.update {
                it.copy(
                    details = details,
                    goals = goals,
                    selectedInterest = interest,
                    selectedDetail = detail,
                    selectedGoal = goal,
                    customDetailName = customDetail,
                    customGoalName = customGoal,
                    savedPath = path,
                )
            }
        }
    }

    /**
     * 목록에서 이름이 같은 항목을 찾습니다.
     * 없으면 직접 입력한 값이므로, 목록의 직접 입력 항목을 고르고 적힌 이름을 따로 들고 있습니다.
     *
     * @return (고른 항목, 직접 적은 이름)
     */
    private fun matchOrDirectInput(
        options: List<InterestOption>,
        name: String?,
    ): Pair<InterestOption?, String?> {
        if (name == null) return null to null

        options.firstOrNull { it.displayName == name }?.let { return it to null }

        return options.firstOrNull { it.isDirectInput() } to name
    }

    private suspend fun fetchOptions(parentId: Int?): List<InterestOption> =
        runCatching { interestService.interests(parentId) }
            .getOrNull()
            ?.interests
            ?.map(::InterestOption)
            .orEmpty()

    fun onDropdownToggle(dropdown: InterestDropdown) {
        _uiState.update { it.copy(expanded = if (it.expanded == dropdown) null else dropdown) }
    }

    fun onDropdownDismiss() {
        _uiState.update { it.copy(expanded = null) }
    }

    /** 관심사를 바꾸면 아래 두 단계는 비웁니다. (iOS 와 동일) */
    fun onInterestSelect(option: InterestOption) {
        _uiState.update {
            it.copy(
                selectedInterest = option,
                selectedDetail = null,
                selectedGoal = null,
                details = emptyList(),
                goals = emptyList(),
                customDetailName = null,
                customGoalName = null,
                expanded = null,
            )
        }

        viewModelScope.launch {
            val details = fetchOptions(parentId = option.id)
            _uiState.update { it.copy(details = details) }
        }
    }

    /** 세부 관심사를 바꾸면 목표를 비우고 새로 불러옵니다. */
    fun onDetailSelect(option: InterestOption) {
        _uiState.update {
            it.copy(
                selectedDetail = option,
                selectedGoal = null,
                goals = emptyList(),
                customDetailName = null,
                customGoalName = null,
                expanded = null,
                directInputTarget = if (option.isDirectInput()) DirectInputTarget.DETAIL else null,
            )
        }

        viewModelScope.launch {
            val goals = fetchOptions(parentId = option.id)
            _uiState.update { it.copy(goals = goals) }
        }
    }

    fun onGoalSelect(option: InterestOption) {
        _uiState.update {
            it.copy(
                selectedGoal = option,
                // 목록에서 고른 목표면 직접 적어 둔 값은 버립니다.
                customGoalName = if (option.isDirectInput()) it.customGoalName else null,
                expanded = null,
                directInputTarget = if (option.isDirectInput()) DirectInputTarget.GOAL else null,
            )
        }
    }

    /** 직접 입력 바텀시트에서 완료를 눌렀을 때입니다. */
    fun onDirectInputConfirm(text: String) {
        _uiState.update { state ->
            when (state.directInputTarget) {
                DirectInputTarget.DETAIL -> state.copy(customDetailName = text, directInputTarget = null)
                DirectInputTarget.GOAL -> state.copy(customGoalName = text, directInputTarget = null)
                null -> state
            }
        }
    }

    /**
     * 직접 입력을 그만두면 고른 항목도 되돌립니다.
     * 이름 없이 "기타" 만 남으면 저장할 수 없기 때문입니다.
     */
    fun onDirectInputDismiss() {
        _uiState.update { state ->
            when (state.directInputTarget) {
                DirectInputTarget.DETAIL ->
                    if (state.customDetailName.isNullOrBlank()) {
                        state.copy(selectedDetail = null, goals = emptyList(), directInputTarget = null)
                    } else {
                        state.copy(directInputTarget = null)
                    }

                DirectInputTarget.GOAL ->
                    if (state.customGoalName.isNullOrBlank()) {
                        state.copy(selectedGoal = null, directInputTarget = null)
                    } else {
                        state.copy(directInputTarget = null)
                    }

                null -> state
            }
        }
    }

    fun onToastShown() {
        _uiState.update { it.copy(toastMessage = null, errorMessage = null) }
    }

    /** @return 바로 나가도 되면 true */
    fun onBackClick(): Boolean {
        if (!_uiState.value.isModified) return true

        _uiState.update { it.copy(isCancelDialogVisible = true) }
        return false
    }

    fun onCancelDialogDismiss() {
        _uiState.update { it.copy(isCancelDialogVisible = false) }
    }

    /**
     * 저장합니다. 보내는 id 는 목표가 있으면 목표 id, 없으면 세부 관심사 id 입니다. (iOS 와 동일)
     */
    fun onCompleteClick() {
        val state = _uiState.value
        val id = memberInterestId ?: return
        val finalId = (if (state.goals.isEmpty()) state.selectedDetail else state.selectedGoal)?.id ?: return
        val path = state.currentPath

        if (path.size != InterestEditUiState.PATH_SIZE) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            val succeeded = runCatching {
                interestService.updateMemberInterest(
                    memberInterestId = id,
                    request = UpdateMemberInterestRequest(interestId = finalId, directFullPath = path),
                )
            }.getOrNull()?.success == true

            _uiState.update {
                if (succeeded) {
                    it.copy(savedPath = path, isSubmitting = false, toastMessage = SAVED)
                } else {
                    it.copy(isSubmitting = false, errorMessage = SAVE_FAILED)
                }
            }
        }
    }

    companion object {
        const val SAVED = "관심사 수정이 완료되었어요"
        const val SAVE_FAILED = "관심사 수정에 실패했어요. 다시 시도해주세요."
    }
}
