package com.swyp.haruup.presentation.mypage.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.component.HaruUpConfirmDialog
import com.swyp.haruup.core.component.HaruUpToast
import com.swyp.haruup.core.component.HaruUpTopBar
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val H_MARGIN = 20.dp
private val TOP_BAR_TO_LABEL = 32.dp
private val LABEL_TO_FIELD = 8.dp
private val FIELD_HEIGHT = 48.dp
private val FIELD_LINE_HEIGHT = 2.dp
private val FIELD_TO_WARNING = 6.dp
private val FIELD_TO_CLEAR = 10.dp
private val CLEAR_SIZE = 24.dp
private val COMPLETE_HEIGHT = 56.dp
private val COMPLETE_RADIUS = 16.dp
private val COMPLETE_BOTTOM = 60.dp
private val TOAST_TO_COMPLETE = 20.dp

/** 입력칸이 포커스를 잃었을 때 밑줄 색의 투명도입니다. (iOS 와 동일) */
private const val UNFOCUSED_LINE_ALPHA = 0.3f

/**
 * 마이페이지 > 프로필 수정. iOS 의 ProfileEditViewController 에 대응합니다.
 *
 * 저장에 성공해도 화면을 닫지 않고 안내만 띄웁니다. (iOS 와 같은 동작)
 */
@Composable
fun ProfileEditScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val back = { if (viewModel.onBackClick()) onBackClick() }

    // 시스템 뒤로 가기도 상단바의 뒤로 가기와 똑같이 한 번 더 묻습니다.
    BackHandler { back() }

    ProfileEditContent(
        uiState = uiState,
        onBackClick = back,
        onNicknameChange = viewModel::onNicknameChange,
        onClearClick = viewModel::onClearClick,
        onCompleteClick = viewModel::onCompleteClick,
        onToastShown = viewModel::onToastShown,
        modifier = modifier,
    )

    if (uiState.isCancelDialogVisible) {
        HaruUpConfirmDialog(
            title = "수정을 취소하시겠습니까?",
            message = "완료를 누르지 않으면,\n수정사항은 변경되지 않아요.",
            confirmText = "예",
            cancelText = "아니오",
            onConfirm = {
                viewModel.onCancelDialogDismiss()
                onBackClick()
            },
            onCancel = viewModel::onCancelDialogDismiss,
        )
    }
}

@Composable
private fun ProfileEditContent(
    uiState: ProfileEditUiState,
    onBackClick: () -> Unit,
    onNicknameChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onToastShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.AppWhite)
            .statusBarsPadding()
            .imePadding(),
    ) {
        HaruUpTopBar(title = "프로필 수정", onBackClick = onBackClick)

        Spacer(Modifier.height(TOP_BAR_TO_LABEL))

        Text(
            text = "닉네임 변경",
            style = HaruUpType.body4,
            color = HaruUpColor.Neutral800,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(LABEL_TO_FIELD))

        NicknameField(
            nickname = uiState.nickname,
            isClearVisible = uiState.isClearVisible,
            onNicknameChange = onNicknameChange,
            onClearClick = onClearClick,
            onDone = {
                keyboard?.hide()
                if (uiState.isCompleteEnabled) onCompleteClick()
            },
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        if (uiState.warning != null) {
            Spacer(Modifier.height(FIELD_TO_WARNING))

            Text(
                text = uiState.warning,
                style = HaruUpType.body4,
                color = HaruUpColor.SecondaryRed200,
                modifier = Modifier.padding(horizontal = H_MARGIN),
            )
        }

        Spacer(Modifier.weight(1f))

        HaruUpToast(
            message = uiState.toastMessage,
            onDismiss = onToastShown,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(TOAST_TO_COMPLETE))

        CompleteButton(
            isEnabled = uiState.isCompleteEnabled,
            onClick = {
                keyboard?.hide()
                onCompleteClick()
            },
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(COMPLETE_BOTTOM))
    }
}

@Composable
private fun NicknameField(
    nickname: String,
    isClearVisible: Boolean,
    onNicknameChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    // iOS 처럼 화면에 들어오면 바로 입력할 수 있게 합니다.
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(FIELD_HEIGHT),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = nickname,
                onValueChange = onNicknameChange,
                singleLine = true,
                textStyle = HaruUpType.body1.copy(color = HaruUpColor.Neutral1000),
                cursorBrush = SolidColor(HaruUpColor.Cta),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
            )

            if (isClearVisible) {
                Spacer(Modifier.width(FIELD_TO_CLEAR))

                Image(
                    painter = painterResource(R.drawable.ic_xmark),
                    contentDescription = "입력 지우기",
                    modifier = Modifier
                        .size(CLEAR_SIZE)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClearClick,
                        ),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(FIELD_LINE_HEIGHT)
                .background(
                    if (isFocused) HaruUpColor.Cta
                    else HaruUpColor.Cta.copy(alpha = UNFOCUSED_LINE_ALPHA)
                ),
        )
    }
}

@Composable
private fun CompleteButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(COMPLETE_RADIUS)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(COMPLETE_HEIGHT)
            .clip(shape)
            .background(if (isEnabled) HaruUpColor.Cta else HaruUpColor.Neutral200, shape)
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "완료", style = HaruUpType.subtitle2, color = HaruUpColor.AppWhite)
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ProfileEditPreview() {
    HaruUpTheme {
        ProfileEditContent(
            uiState = ProfileEditUiState(nickname = "하루나루", savedNickname = "하루"),
            onBackClick = {}, onNicknameChange = {}, onClearClick = {},
            onCompleteClick = {}, onToastShown = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ProfileEditWarningPreview() {
    HaruUpTheme {
        ProfileEditContent(
            uiState = ProfileEditUiState(
                nickname = "하",
                savedNickname = "하루나루",
                warning = "*2~10자로 입력해주세요.",
            ),
            onBackClick = {}, onNicknameChange = {}, onClearClick = {},
            onCompleteClick = {}, onToastShown = {},
        )
    }
}
