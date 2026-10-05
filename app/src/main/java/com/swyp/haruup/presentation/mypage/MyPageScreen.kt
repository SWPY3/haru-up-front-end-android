package com.swyp.haruup.presentation.mypage

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.core.component.HaruUpConfirmDialog
import com.swyp.haruup.core.component.HaruUpNoticeDialog
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mypage.component.MyPageMenuList
import com.swyp.haruup.presentation.mypage.component.MyPageProfileHeader

private val H_MARGIN = 20.dp
private val TITLE_TOP = 20.dp
private val TITLE_TO_PROFILE = 30.dp
private val PROFILE_TO_MENU = 24.dp
private val MENU_TO_VERSION = 30.dp
private val VERSION_LEADING = 20.dp
private val CONTENT_BOTTOM = 40.dp

/**
 * 메인 탭의 마이페이지. iOS 의 MyPageViewController 에 대응합니다.
 *
 * iOS 에는 직업 라벨과 GOAL 카드, "관심사 수정" 메뉴가 더 있지만
 * 모두 `isHidden = true` 로 가려 둔 상태라 실제 앱에서는 보이지 않습니다. 그래서 옮기지 않았습니다.
 */
@Composable
fun MyPageScreen(
    onEditProfileClick: () -> Unit,
    onNotificationSettingClick: () -> Unit,
    onSignedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyPageViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 프로필 수정에서 돌아왔을 때 바뀐 닉네임이 바로 보이도록 다시 받아옵니다.
    LifecycleResumeEffect(viewModel) {
        viewModel.refreshProfile()
        onPauseOrDispose { }
    }

    MyPageContent(
        uiState = uiState,
        onEditProfileClick = onEditProfileClick,
        onMenuClick = { menu ->
            when {
                menu.url != null ->
                    CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(menu.url))

                menu == MyPageMenu.NOTIFICATION_SETTING -> onNotificationSettingClick()

                else -> viewModel.onMenuClick(menu)
            }
        },
        modifier = modifier,
    )

    MyPageDialogHost(
        dialog = uiState.dialog,
        onDismiss = viewModel::onDialogDismiss,
        onLogoutConfirm = { viewModel.onLogoutConfirm(onSignedOut) },
        onWithdrawConfirm = viewModel::onWithdrawConfirm,
        onWithdrawSuccessConfirm = { viewModel.onWithdrawSuccessConfirm(onSignedOut) },
    )
}

@Composable
private fun MyPageContent(
    uiState: MyPageUiState,
    onEditProfileClick: () -> Unit,
    onMenuClick: (MyPageMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(top = TITLE_TOP, bottom = CONTENT_BOTTOM),
    ) {
        Text(
            text = "마이페이지",
            style = HaruUpType.title3,
            color = HaruUpColor.AppBlack,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(TITLE_TO_PROFILE))

        MyPageProfileHeader(
            displayName = uiState.displayName,
            characterId = uiState.characterId,
            onEditClick = onEditProfileClick,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(PROFILE_TO_MENU))

        MyPageMenuList(
            menus = MyPageMenu.entries,
            onMenuClick = onMenuClick,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(MENU_TO_VERSION))

        Text(
            text = uiState.versionText,
            style = HaruUpType.body4,
            color = HaruUpColor.Neutral700,
            modifier = Modifier.padding(start = H_MARGIN + VERSION_LEADING),
        )
    }
}

/** 한 번에 하나만 뜨므로 when 으로 갈라 띄웁니다. */
@Composable
private fun MyPageDialogHost(
    dialog: MyPageDialog?,
    onDismiss: () -> Unit,
    onLogoutConfirm: () -> Unit,
    onWithdrawConfirm: () -> Unit,
    onWithdrawSuccessConfirm: () -> Unit,
) {
    when (dialog) {
        null -> Unit

        MyPageDialog.Logout -> HaruUpConfirmDialog(
            title = "로그아웃을 진행할까요?",
            message = "다음 접속 시 계정 정보를\n다시 입력해야 해요.",
            confirmText = "예",
            cancelText = "아니오",
            onConfirm = onLogoutConfirm,
            onCancel = onDismiss,
        )

        MyPageDialog.Withdraw -> HaruUpConfirmDialog(
            title = "😢 정말 저희를 떠나실 건가요?",
            message = "탈퇴 시, 모든 기록이 사라지며\n복구할 수 없어요.",
            confirmText = "탈퇴하기",
            cancelText = "취소",
            onConfirm = onWithdrawConfirm,
            onCancel = onDismiss,
        )

        MyPageDialog.WithdrawSuccess -> HaruUpNoticeDialog(
            title = "탈퇴가 완료되었습니다.",
            message = "더 좋은 서비스를 준비할게요.\n다음에 다시 만나요!",
            confirmText = "확인",
            onConfirm = onWithdrawSuccessConfirm,
        )

        is MyPageDialog.Error -> HaruUpNoticeDialog(
            title = "오류",
            message = dialog.message,
            confirmText = "확인",
            onConfirm = onDismiss,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun MyPagePreview() {
    HaruUpTheme {
        MyPageContent(
            uiState = MyPageUiState(nickname = "하루나루", characterId = 1),
            onEditProfileClick = {},
            onMenuClick = {},
        )
    }
}
