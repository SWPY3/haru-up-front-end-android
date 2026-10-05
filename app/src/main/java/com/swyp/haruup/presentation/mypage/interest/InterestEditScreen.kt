package com.swyp.haruup.presentation.mypage.interest

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.core.component.HaruUpConfirmDialog
import com.swyp.haruup.core.component.HaruUpDropdown
import com.swyp.haruup.core.component.HaruUpToast
import com.swyp.haruup.core.component.HaruUpTopBar
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mypage.interest.component.DirectInputSheet

private val H_MARGIN = 20.dp
private val TOP_BAR_TO_DESCRIPTION = 32.dp
private val DESCRIPTION_TO_FIRST = 32.dp
private val BETWEEN_FIELDS = 24.dp
private val COMPLETE_HEIGHT = 56.dp
private val COMPLETE_RADIUS = 16.dp
private val COMPLETE_BOTTOM = 60.dp
private val TOAST_TO_COMPLETE = 20.dp

/**
 * 마이페이지 > 관심사 수정. iOS 의 InterestEditViewController 에 대응합니다.
 *
 * 관심사 > 세부 관심사 > 목표를 차례로 고르고, 위 단계를 바꾸면 아래는 비워집니다.
 * "기타" 나 "직접 입력" 을 고르면 직접 적는 바텀시트가 열립니다.
 */
@Composable
fun InterestEditScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InterestEditViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val back = { if (viewModel.onBackClick()) onBackClick() }

    BackHandler { back() }

    InterestEditContent(
        uiState = uiState,
        onBackClick = back,
        onDropdownToggle = viewModel::onDropdownToggle,
        onDropdownDismiss = viewModel::onDropdownDismiss,
        onInterestSelect = viewModel::onInterestSelect,
        onDetailSelect = viewModel::onDetailSelect,
        onGoalSelect = viewModel::onGoalSelect,
        onCompleteClick = viewModel::onCompleteClick,
        onToastShown = viewModel::onToastShown,
        modifier = modifier,
    )

    uiState.directInputTarget?.let { target ->
        DirectInputSheet(
            title = target.title,
            initialText = when (target) {
                DirectInputTarget.DETAIL -> uiState.customDetailName
                DirectInputTarget.GOAL -> uiState.customGoalName
            },
            onConfirm = viewModel::onDirectInputConfirm,
            onDismiss = viewModel::onDirectInputDismiss,
        )
    }

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
private fun InterestEditContent(
    uiState: InterestEditUiState,
    onBackClick: () -> Unit,
    onDropdownToggle: (InterestDropdown) -> Unit,
    onDropdownDismiss: () -> Unit,
    onInterestSelect: (InterestOption) -> Unit,
    onDetailSelect: (InterestOption) -> Unit,
    onGoalSelect: (InterestOption) -> Unit,
    onCompleteClick: () -> Unit,
    onToastShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.AppWhite)
            .statusBarsPadding()
            // 펼친 드롭다운 바깥을 누르면 접습니다. (iOS 의 화면 탭 제스처와 같습니다)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDropdownDismiss,
            ),
    ) {
        HaruUpTopBar(title = "관심사 수정", onBackClick = onBackClick)

        Spacer(Modifier.height(TOP_BAR_TO_DESCRIPTION))

        Text(
            text = "관심사를 수정해도 캐릭터의 성장도는 유지돼요.",
            style = HaruUpType.body1,
            color = HaruUpColor.Neutral800,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(DESCRIPTION_TO_FIRST))

        HaruUpDropdown(
            label = "관심사",
            placeholder = "관심사 선택",
            selectedName = uiState.selectedInterest?.displayName,
            items = uiState.interests,
            selectedId = uiState.selectedInterest?.id,
            isExpanded = uiState.expanded == InterestDropdown.INTEREST,
            onToggle = { onDropdownToggle(InterestDropdown.INTEREST) },
            onSelect = onInterestSelect,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(BETWEEN_FIELDS))

        HaruUpDropdown(
            label = "세부 관심사",
            placeholder = "세부 관심사 선택",
            selectedName = uiState.detailDisplayName,
            items = uiState.details,
            selectedId = uiState.selectedDetail?.id,
            isExpanded = uiState.expanded == InterestDropdown.DETAIL,
            onToggle = { onDropdownToggle(InterestDropdown.DETAIL) },
            onSelect = onDetailSelect,
            isEnabled = uiState.selectedInterest != null,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(BETWEEN_FIELDS))

        HaruUpDropdown(
            label = "목표",
            placeholder = "목표 선택",
            selectedName = uiState.goalDisplayName,
            items = uiState.goals,
            selectedId = uiState.selectedGoal?.id,
            isExpanded = uiState.expanded == InterestDropdown.GOAL,
            onToggle = { onDropdownToggle(InterestDropdown.GOAL) },
            onSelect = onGoalSelect,
            isEnabled = uiState.isGoalEnabled,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.weight(1f))

        HaruUpToast(
            message = uiState.toastMessage ?: uiState.errorMessage,
            onDismiss = onToastShown,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(TOAST_TO_COMPLETE))

        CompleteButton(
            isEnabled = uiState.isCompleteEnabled,
            onClick = onCompleteClick,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(COMPLETE_BOTTOM))
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
private fun InterestEditPreview() {
    val interests = listOf(InterestOption(1, "외국어 공부"), InterestOption(2, "자격증 공부"))
    val details = listOf(InterestOption(11, "영어"), InterestOption(12, "중국어"))
    val goals = listOf(InterestOption(21, "회화 공부"), InterestOption(22, "직접 입력할게요"))

    HaruUpTheme {
        InterestEditContent(
            uiState = InterestEditUiState(
                interests = interests,
                details = details,
                goals = goals,
                selectedInterest = interests[0],
                selectedDetail = details[0],
                selectedGoal = goals[0],
                savedPath = listOf("외국어 공부", "영어", "회화 공부"),
            ),
            onBackClick = {}, onDropdownToggle = {}, onDropdownDismiss = {},
            onInterestSelect = {}, onDetailSelect = {}, onGoalSelect = {},
            onCompleteClick = {}, onToastShown = {},
        )
    }
}
