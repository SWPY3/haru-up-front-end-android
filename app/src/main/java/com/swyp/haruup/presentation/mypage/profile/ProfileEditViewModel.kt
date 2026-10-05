package com.swyp.haruup.presentation.mypage.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.core.util.NicknameValidation
import com.swyp.haruup.core.util.NicknameValidator
import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.data.model.NicknameDuplicateRequest
import com.swyp.haruup.data.model.ProfileData
import com.swyp.haruup.data.model.UpdateProfileRequest
import com.swyp.haruup.network.service.AuthService
import com.swyp.haruup.network.service.ChatbotService
import com.swyp.haruup.network.service.ProfileService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileEditUiState(
    val nickname: String = "",
    /** 서버에 저장되어 있는 닉네임. 이 값과 달라야 완료 버튼이 켜집니다. */
    val savedNickname: String = "",
    /** 닉네임 입력칸 아래에 뜨는 빨간 안내 문구 */
    val warning: String? = null,
    val isSubmitting: Boolean = false,
    /** 저장에 성공하면 잠깐 떴다 사라지는 안내 */
    val toastMessage: String? = null,
    /** 뒤로 가기를 눌렀는데 고친 내용이 남아 있을 때 뜨는 확인창 */
    val isCancelDialogVisible: Boolean = false,
) {
    /** 저장된 닉네임과 달라야 완료할 수 있습니다. (iOS 와 같은 조건) */
    val isCompleteEnabled: Boolean = nickname.trim() != savedNickname && !isSubmitting

    val isClearVisible: Boolean = nickname.isNotEmpty()
}

/**
 * 마이페이지 > 프로필 수정. iOS 의 ProfileEditViewController 에 대응합니다.
 *
 * iOS 에는 직업 / 세부 직무 선택이 더 있지만 setupUI 에서 isHidden = true 로 가려 둔 뒤
 * 다시 켜는 곳이 없어 실제 앱에서는 닉네임만 고칠 수 있습니다. 그래서 닉네임만 옮겼습니다.
 */
@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val authService: AuthService,
    private val chatbotService: ChatbotService,
    private val profileService: ProfileService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    /** 고치지 않고 돌려보낼 직업 정보입니다. 화면에는 쓰지 않습니다. */
    private var profile: ProfileData? = null

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val data = runCatching { authService.profile() }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data
                ?: return@launch

            profile = data
            _uiState.update { it.copy(nickname = data.nickname, savedNickname = data.nickname) }
        }
    }

    /** 입력을 고치면 떠 있던 안내 문구를 지웁니다. (iOS 와 동일) */
    fun onNicknameChange(nickname: String) {
        _uiState.update { it.copy(nickname = nickname, warning = null) }
    }

    fun onClearClick() {
        _uiState.update { it.copy(nickname = "", warning = null) }
    }

    fun onToastShown() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    /**
     * 뒤로 가기. 고친 내용이 남아 있으면 바로 나가지 않고 한 번 더 묻습니다.
     * @return 바로 나가도 되면 true
     */
    fun onBackClick(): Boolean {
        val state = _uiState.value

        if (state.nickname.trim() == state.savedNickname) return true

        _uiState.update { it.copy(isCancelDialogVisible = true) }
        return false
    }

    fun onCancelDialogDismiss() {
        _uiState.update { it.copy(isCancelDialogVisible = false) }
    }

    /**
     * 완료. 로컬 검사 → 중복 확인 → 저장 순서로 진행하며 어느 단계든 걸리면 거기서 멈춥니다.
     *
     * 저장에 성공해도 화면을 닫지 않고 안내만 띄웁니다. (iOS 와 같은 동작)
     */
    fun onCompleteClick() {
        val nickname = _uiState.value.nickname.trim()

        NicknameValidator.check(nickname).let { result ->
            if (result != NicknameValidation.VALID) {
                _uiState.update { it.copy(warning = warningOf(result)) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            if (!isNicknameAvailable(nickname)) {
                _uiState.update { it.copy(warning = DUPLICATED, isSubmitting = false) }
                return@launch
            }

            if (!saveNickname(nickname)) {
                _uiState.update { it.copy(warning = SAVE_FAILED, isSubmitting = false) }
                return@launch
            }

            _uiState.update {
                it.copy(
                    savedNickname = nickname,
                    nickname = nickname,
                    warning = null,
                    isSubmitting = false,
                    toastMessage = NICKNAME_CHANGED,
                )
            }
        }
    }

    /** 중복 확인에서 success 가 true 면 쓸 수 있는 닉네임입니다. */
    private suspend fun isNicknameAvailable(nickname: String): Boolean =
        runCatching { chatbotService.checkNicknameDuplicate(NicknameDuplicateRequest(nickname)) }
            .getOrNull()
            ?.success == true

    private suspend fun saveNickname(nickname: String): Boolean {
        val refreshToken = tokenStorage.getRefreshToken() ?: return false

        return runCatching {
            profileService.updateProfile(
                refreshToken = refreshToken,
                request = UpdateProfileRequest(
                    nickname = nickname,
                    // 직업은 고칠 수 없으니 받아 둔 값을 그대로 돌려보냅니다.
                    jobId = profile?.jobId,
                    jobDetailId = profile?.jobDetailId,
                ),
            )
        }.getOrNull()?.success == true
    }

    companion object {
        const val DUPLICATED = "*이미 존재하는 닉네임입니다."
        const val SAVE_FAILED = "*닉네임을 저장하지 못했어요. 잠시 후 다시 시도해주세요."
        const val NICKNAME_CHANGED = "닉네임 변경이 완료되었어요"

        /** 프로필 수정 화면의 안내 문구입니다. 큐레이션과 문구가 달라 여기서 따로 정합니다. */
        fun warningOf(result: NicknameValidation): String? = when (result) {
            NicknameValidation.TOO_SHORT, NicknameValidation.TOO_LONG -> "*2~10자로 입력해주세요."
            NicknameValidation.NOT_KOREAN -> "*한글만 입력해주세요."
            NicknameValidation.INCOMPLETE_KOREAN -> "*올바른 형태로 입력해주세요."
            NicknameValidation.VALID -> null
        }
    }
}
