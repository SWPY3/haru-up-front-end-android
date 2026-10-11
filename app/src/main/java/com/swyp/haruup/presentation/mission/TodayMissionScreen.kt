package com.swyp.haruup.presentation.mission

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.component.HaruUpToast
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mission.component.MissionCard

private val H_MARGIN = 20.dp
private val RETRY_ICON_SIZE = 20.dp
private val RETRY_ICON_TO_TEXT = 4.dp
private val HEADER_TOP = 16.dp
private val TITLE_TO_SUBTITLE = 4.dp
private val SUBTITLE_TO_FILTER = 18.dp
private val FILTER_HEIGHT = 36.dp
private val FILTER_WIDTH = 57.dp
private val FILTER_SPACING = 4.dp
private val FILTER_TO_LIST = 16.dp
private val BOTTOM_RADIUS = 24.dp
private val BOTTOM_V_PADDING = 16.dp
private val COMPLETE_BUTTON_WIDTH = 152.dp
private val COMPLETE_BUTTON_HEIGHT = 56.dp
private val CORNER_RADIUS = 16.dp
private val CLOSE_BAR_HEIGHT = 54.dp
private val CLOSE_BUTTON_SIZE = 44.dp
private val CLOSE_BUTTON_TRAILING = 12.dp
private val SKELETON_HEIGHT = 110.dp
private val SKELETON_SPACING = 12.dp
private val TOAST_BOTTOM = 12.dp

/**
 * 큐레이션 직후 미션 선택 화면.
 * iOS 의 TodayMissionListViewController 중 챗봇 플로우 경로에 대응합니다.
 *
 * 챗봇이 만들어 준 미션을 그대로 보여주고 추천 API 를 다시 부르지 않습니다.
 */
@Composable
fun TodayMissionScreen(
    missions: List<MissionItem>,
    onCompleted: (selectedIds: List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayMissionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(missions) { viewModel.setMissions(missions) }

    TodayMissionContent(
        uiState = uiState,
        onRetryClick = viewModel::onRetryClick,
        onFilterClick = viewModel::onFilterClick,
        onMissionClick = viewModel::onMissionClick,
        onCompleteClick = { viewModel.onCompleteClick(onCompleted) },
        onCloseClick = {},
        onReloadClick = viewModel::onReloadClick,
        onErrorShown = viewModel::onErrorShown,
        modifier = modifier,
    )
}

/**
 * 홈에서 미션을 고르거나 더할 때의 화면.
 * iOS 의 TodayMissionCoordinator 가 띄우던 소개 → 목록 흐름에 대응합니다.
 *
 * @param fixedIds 오늘 이미 고른 미션. 비어 있으면 처음 고르러 온 것이라 소개 화면을 보여 줄 수 있습니다.
 */
@Composable
fun HomeMissionSelectScreen(
    fixedIds: List<Int>,
    onClose: () -> Unit,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayMissionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.startFromHome(fixedIds) }

    if (uiState.isIntroVisible) {
        TodayMissionIntroScreen(onConfirm = viewModel::onIntroConfirm, modifier = modifier)
        return
    }

    TodayMissionContent(
        uiState = uiState,
        onRetryClick = viewModel::onRetryClick,
        onFilterClick = viewModel::onFilterClick,
        onMissionClick = viewModel::onMissionClick,
        onCompleteClick = { viewModel.onCompleteClick { onCompleted() } },
        onCloseClick = onClose,
        onReloadClick = viewModel::onReloadClick,
        onErrorShown = viewModel::onErrorShown,
        modifier = modifier,
    )
}

@Composable
private fun TodayMissionContent(
    uiState: TodayMissionUiState,
    onRetryClick: () -> Unit,
    onFilterClick: (MissionDifficultyFilter) -> Unit,
    onMissionClick: (Int) -> Unit,
    onCompleteClick: () -> Unit,
    onCloseClick: () -> Unit,
    onReloadClick: () -> Unit,
    onErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10)
            .statusBarsPadding(),
    ) {
        if (uiState.isClosable) CloseBar(onCloseClick = onCloseClick)

        MissionListHeader(
            filter = uiState.filter,
            retryLabel = uiState.retryLabel,
            isRetryEnabled = uiState.isRetryEnabled,
            onRetryClick = onRetryClick,
            onFilterClick = onFilterClick,
        )

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isLoading && uiState.missions.isEmpty()) {
                MissionSkeletonList()
            } else if (uiState.filteredMissions.isEmpty()) {
                EmptyMissionMessage(
                    isLoadFailed = uiState.isLoadFailed,
                    onReloadClick = onReloadClick,
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(bottom = 12.dp)) {
                    items(items = uiState.filteredMissions, key = { it.id }) { mission ->
                        MissionCard(
                            mission = mission,
                            isSelected = uiState.isSelected(mission.id),
                            isDisabled = uiState.isDisabled(mission.id),
                            onClick = { onMissionClick(mission.id) },
                        )
                    }
                }
            }

            HaruUpToast(
                message = uiState.errorMessage,
                onDismiss = onErrorShown,
                showCheckIcon = false,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = H_MARGIN)
                    .offset(y = -TOAST_BOTTOM),
            )
        }

        SelectionBottomBar(
            remainingCount = uiState.remainingCount,
            isCompleteEnabled = uiState.isCompleteEnabled,
            onCompleteClick = onCompleteClick,
        )
    }
}

/** 홈에서 들어왔을 때만 보이는 오른쪽 위 닫기 버튼 */
@Composable
private fun CloseBar(onCloseClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CLOSE_BAR_HEIGHT)
            .padding(end = CLOSE_BUTTON_TRAILING),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Box(
            modifier = Modifier
                .size(CLOSE_BUTTON_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCloseClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(painter = painterResource(R.drawable.ic_xmark), contentDescription = "닫기")
        }
    }
}

/** 추천을 받아오는 동안 카드 자리를 미리 잡아 둡니다. (iOS SkeletonMissionCell 대응) */
@Composable
private fun MissionSkeletonList() {
    Column(
        modifier = Modifier.padding(horizontal = H_MARGIN),
        verticalArrangement = Arrangement.spacedBy(SKELETON_SPACING),
    ) {
        repeat(TodayMissionUiState.MAX_SELECTION) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SKELETON_HEIGHT)
                    .clip(RoundedCornerShape(CORNER_RADIUS))
                    .background(HaruUpColor.Neutral50),
            )
        }
    }
}

/** 추천이 비었을 때. 받아오지 못한 경우에만 다시 불러오기 버튼을 둡니다. */
@Composable
private fun EmptyMissionMessage(
    isLoadFailed: Boolean,
    onReloadClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "추천된 미션이 없어요.\n잠시 후 다시 시도해주세요.",
            style = HaruUpType.body1,
            color = HaruUpColor.Neutral600,
            textAlign = TextAlign.Center,
        )

        if (isLoadFailed) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "다시 불러오기",
                style = HaruUpType.subtitle2,
                color = HaruUpColor.Cta,
                modifier = Modifier
                    .clip(RoundedCornerShape(CORNER_RADIUS))
                    .clickable(onClick = onReloadClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

/**
 * 제목 오른쪽의 "다른 추천 N/5회" 버튼.
 * 횟수를 다 쓰면 흐려지고 눌리지 않습니다. (iOS 와 같은 처리)
 */
@Composable
private fun RetryButton(
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val color = if (isEnabled) HaruUpColor.Neutral500 else HaruUpColor.Neutral300

    Row(
        modifier = Modifier.clickable(
            enabled = isEnabled,
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        horizontalArrangement = Arrangement.spacedBy(RETRY_ICON_TO_TEXT),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_retry),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(RETRY_ICON_SIZE),
        )

        Text(text = label, style = HaruUpType.retryButton, color = color)
    }
}

@Composable
private fun MissionListHeader(
    filter: MissionDifficultyFilter,
    retryLabel: String,
    isRetryEnabled: Boolean,
    onRetryClick: () -> Unit,
    onFilterClick: (MissionDifficultyFilter) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(Modifier.height(HEADER_TOP))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = H_MARGIN),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "AI 추천미션",
                style = HaruUpType.title3,
                color = HaruUpColor.AppBlack,
                modifier = Modifier.weight(1f),
            )

            RetryButton(
                label = retryLabel,
                isEnabled = isRetryEnabled,
                onClick = onRetryClick,
            )
        }

        Spacer(Modifier.height(TITLE_TO_SUBTITLE))

        Text(
            text = "미션은 하루 최대 ${TodayMissionUiState.MAX_SELECTION}개까지 선택할 수 있어요.",
            style = HaruUpType.body3,
            color = HaruUpColor.Neutral500,
            modifier = Modifier.padding(horizontal = H_MARGIN),
        )

        Spacer(Modifier.height(SUBTITLE_TO_FILTER))

        Row(
            modifier = Modifier.padding(horizontal = H_MARGIN),
            horizontalArrangement = Arrangement.spacedBy(FILTER_SPACING),
        ) {
            MissionDifficultyFilter.entries.forEach { entry ->
                FilterChip(
                    text = entry.label,
                    isSelected = entry == filter,
                    onClick = { onFilterClick(entry) },
                )
            }
        }

        Spacer(Modifier.height(FILTER_TO_LIST))
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(32.dp)

    Box(
        modifier = Modifier
            .width(FILTER_WIDTH)
            .height(FILTER_HEIGHT)
            .clip(shape)
            .background(
                color = if (isSelected) HaruUpColor.AppBlack else HaruUpColor.AppWhite,
                shape = shape,
            )
            .border(
                border = BorderStroke(
                    1.dp,
                    if (isSelected) HaruUpColor.Neutral1000 else HaruUpColor.Neutral50,
                ),
                shape = shape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HaruUpType.body3,
            color = if (isSelected) HaruUpColor.AppWhite else HaruUpColor.Neutral700,
        )
    }
}

@Composable
private fun SelectionBottomBar(
    remainingCount: Int,
    isCompleteEnabled: Boolean,
    onCompleteClick: () -> Unit,
) {
    val shape = RoundedCornerShape(topStart = BOTTOM_RADIUS, topEnd = BOTTOM_RADIUS)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .navigationBarsPadding()
            .padding(horizontal = H_MARGIN, vertical = BOTTOM_V_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "선택 가능한 미션",
                style = HaruUpType.body3,
                color = HaruUpColor.AppBlack,
            )
            Text(
                text = "${remainingCount}개",
                style = HaruUpType.title3,
                // 다 골랐으면 강조를 뺍니다.
                color = if (remainingCount == 0) HaruUpColor.Neutral400 else HaruUpColor.Cta,
            )
        }

        Button(
            onClick = onCompleteClick,
            enabled = isCompleteEnabled,
            shape = RoundedCornerShape(CORNER_RADIUS),
            colors = ButtonDefaults.buttonColors(
                containerColor = HaruUpColor.Cta,
                contentColor = HaruUpColor.AppWhite,
                disabledContainerColor = HaruUpColor.Neutral200,
                disabledContentColor = HaruUpColor.AppWhite,
            ),
            modifier = Modifier.size(width = COMPLETE_BUTTON_WIDTH, height = COMPLETE_BUTTON_HEIGHT),
        ) {
            Text(text = "선택 완료", style = HaruUpType.subtitle2)
        }
    }
}

private val previewMissions = listOf(
    MissionItem(1, "신용카드와 체크카드 사용 비율 조정하기", null, MissionDifficulty.VERY_HIGH, 250),
    MissionItem(2, "고정 지출과 변동 지출 구분하기", "한 달 내역을 두 갈래로 나눠보세요.", MissionDifficulty.HIGH, 200),
    MissionItem(3, "한 달 지출 내역 분석하기", null, MissionDifficulty.MEDIUM_HIGH, 150),
    MissionItem(4, "일주일 지출 예산 세우기", null, MissionDifficulty.MEDIUM, 100),
    MissionItem(5, "가계부 앱 설치하기", null, MissionDifficulty.LOW, 50),
)

@Preview(showBackground = true, device = "id:pixel_7", name = "미선택")
@Composable
private fun TodayMissionPreview() {
    HaruUpTheme {
        TodayMissionContent(
            uiState = TodayMissionUiState(missions = previewMissions),
            onRetryClick = {}, onFilterClick = {}, onMissionClick = {}, onCompleteClick = {},
            onCloseClick = {}, onReloadClick = {}, onErrorShown = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "5개 선택")
@Composable
private fun TodayMissionFullPreview() {
    HaruUpTheme {
        TodayMissionContent(
            uiState = TodayMissionUiState(
                missions = previewMissions,
                selectedIds = previewMissions.map { it.id }.toSet(),
            ),
            onRetryClick = {}, onFilterClick = {}, onMissionClick = {}, onCompleteClick = {},
            onCloseClick = {}, onReloadClick = {}, onErrorShown = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "홈에서 추가")
@Composable
private fun HomeMissionSelectPreview() {
    HaruUpTheme {
        TodayMissionContent(
            uiState = TodayMissionUiState(missions = previewMissions.take(3), fixedCount = 2, isClosable = true),
            onRetryClick = {}, onFilterClick = {}, onMissionClick = {}, onCompleteClick = {},
            onCloseClick = {}, onReloadClick = {}, onErrorShown = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "불러오는 중")
@Composable
private fun HomeMissionLoadingPreview() {
    HaruUpTheme {
        TodayMissionContent(
            uiState = TodayMissionUiState(isLoading = true, isClosable = true),
            onRetryClick = {}, onFilterClick = {}, onMissionClick = {}, onCompleteClick = {},
            onCloseClick = {}, onReloadClick = {}, onErrorShown = {},
        )
    }
}
