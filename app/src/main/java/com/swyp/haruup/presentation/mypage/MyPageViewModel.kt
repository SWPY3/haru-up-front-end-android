package com.swyp.haruup.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swyp.haruup.BuildConfig
import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.network.service.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyPageUiState(
    val nickname: String? = null,
    /** 1 = 하루, 2 = 나루 */
    val characterId: Int = DEFAULT_CHARACTER_ID,
    val dialog: MyPageDialog? = null,
    /** 로그아웃·탈퇴 처리 중에는 버튼을 다시 누를 수 없게 막습니다. */
    val isProcessing: Boolean = false,
) {
    /** 프로필을 못 받아왔으면 iOS 와 같이 "사용자" 로 둡니다. */
    val displayName: String = nickname?.let { "${it}님" } ?: "사용자"

    val versionText: String = "버전.v.${BuildConfig.VERSION_NAME}"

    companion object {
        const val DEFAULT_CHARACTER_ID = 1
    }
}

/**
 * 메인 탭의 마이페이지. iOS 의 MyPageViewModel 에 대응합니다.
 */
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val authService: AuthService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    init {
        refreshProfile()
    }

    /**
     * 프로필을 다시 받아옵니다.
     * 프로필 수정 화면에서 돌아왔을 때 바뀐 닉네임이 바로 보이도록 화면이 올라올 때마다 호출합니다.
     * (iOS 가 viewWillAppear 마다 조회하는 것과 같습니다)
     */
    fun refreshProfile() {
        viewModelScope.launch {
            val profile = runCatching { authService.profile() }
                .getOrNull()
                ?.takeIf { it.success }
                ?.data
                ?: return@launch

            _uiState.update {
                it.copy(nickname = profile.nickname, characterId = profile.characterId)
            }
        }
    }

    fun onMenuClick(menu: MyPageMenu) {
        when (menu) {
            MyPageMenu.LOGOUT -> _uiState.update { it.copy(dialog = MyPageDialog.Logout) }
            MyPageMenu.WITHDRAW -> _uiState.update { it.copy(dialog = MyPageDialog.Withdraw) }
            // 나머지는 화면 이동이나 외부 브라우저라 화면 쪽에서 처리합니다.
            else -> Unit
        }
    }

    fun onDialogDismiss() {
        _uiState.update { it.copy(dialog = null) }
    }

    /**
     * 로그아웃합니다. iOS 와 달리 refreshToken 을 본문에 싣지 않고
     * AuthInterceptor 가 붙이는 Authorization 헤더를 씁니다.
     */
    fun onLogoutConfirm(onSignedOut: () -> Unit) {
        runSignOut(
            request = { authService.logout().success },
            onSuccess = onSignedOut,
            failureMessage = "로그아웃에 실패했습니다.",
        )
    }

    /** 탈퇴한 뒤에는 바로 나가지 않고 완료 알림을 먼저 띄웁니다. (iOS 와 동일) */
    fun onWithdrawConfirm() {
        runSignOut(
            request = { authService.withdraw().success },
            onSuccess = { _uiState.update { it.copy(dialog = MyPageDialog.WithdrawSuccess) } },
            failureMessage = "탈퇴에 실패했습니다.",
        )
    }

    fun onWithdrawSuccessConfirm(onSignedOut: () -> Unit) {
        _uiState.update { it.copy(dialog = null) }
        onSignedOut()
    }

    /**
     * 로그아웃과 탈퇴는 "서버에 알리고 → 로컬 토큰을 지운다" 는 흐름이 같습니다.
     *
     * 서버 호출이 실패하면 토큰을 지우지 않습니다.
     * 지워 버리면 사용자는 로그아웃된 것처럼 보이지만 서버에는 세션이 남아
     * 양쪽 상태가 어긋나기 때문입니다. (iOS 와 같은 정책)
     */
    private fun runSignOut(
        request: suspend () -> Boolean,
        onSuccess: () -> Unit,
        failureMessage: String,
    ) {
        if (_uiState.value.isProcessing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val succeeded = runCatching { request() }.getOrDefault(false)

            if (succeeded) {
                tokenStorage.clear()
                _uiState.update { it.copy(dialog = null, isProcessing = false) }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(dialog = MyPageDialog.Error(failureMessage), isProcessing = false)
                }
            }
        }
    }
}
