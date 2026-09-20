package com.swyp.haruup.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.presentation.home.component.AddMissionRow
import com.swyp.haruup.presentation.home.component.EmptyMissionCard
import com.swyp.haruup.presentation.home.component.HomeHeader
import com.swyp.haruup.presentation.home.component.HomeMissionCard
import com.swyp.haruup.presentation.home.component.MissionSectionHeader
import com.swyp.haruup.presentation.mission.MissionDifficulty
import com.swyp.haruup.presentation.mission.MissionItem

/** 헤더와 섹션 머리말 사이 간격. iOS sectionHeaderTopPadding 과 같습니다. */
private val HEADER_TO_SECTION = 28.dp
private val LIST_BOTTOM_PADDING = 40.dp
private val EMPTY_TO_ADD_SPACING = 16.dp

/**
 * 메인 탭의 홈. iOS 의 HomeViewController 에 대응합니다.
 *
 * 미션 상세·연속 달성 바텀시트는 이어서 붙입니다.
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onBubbleClick = viewModel::onBubbleClick,
        onInfoClick = viewModel::onInfoClick,
        modifier = modifier,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onBubbleClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10),
        contentPadding = PaddingValues(bottom = LIST_BOTTOM_PADDING),
    ) {
        item(key = "header") {
            HomeHeader(
                memberInfo = uiState.memberInfo,
                bubbleText = uiState.bubbleText,
                challengeDay = uiState.challengeDay,
                isDaytime = HomeViewModel.isDaytime(),
                onBubbleClick = onBubbleClick,
                // TODO: 연속 달성 바텀시트를 띄우도록 교체
                onChallengeClick = {},
            )
            Spacer(Modifier.height(HEADER_TO_SECTION))
        }

        item(key = "section_header") {
            MissionSectionHeader(
                isTooltipVisible = uiState.isTooltipVisible,
                onInfoClick = onInfoClick,
            )
        }

        if (!uiState.hasMissions) {
            item(key = "empty") {
                // TODO: 미션 선택 화면으로 이동하도록 교체
                EmptyMissionCard(onAddClick = {})
            }
        } else {
            items(items = uiState.todayMissions, key = { it.id }) { mission ->
                HomeMissionCard(
                    mission = mission,
                    isCompleted = uiState.isCompleted(mission.id),
                    // TODO: 미션 상세 바텀시트를 띄우도록 교체
                    onSettingClick = {},
                )
            }

            if (uiState.canAddMission) {
                item(key = "add") {
                    Spacer(Modifier.height(EMPTY_TO_ADD_SPACING))
                    // TODO: 미션 추가 화면으로 이동하도록 교체
                    AddMissionRow(onAddClick = {})
                }
            }
        }
    }
}

private val previewMemberInfo = HomeMemberInfo(
    characterId = 1,
    level = 3,
    nickname = "영현",
    currentExp = 50,
    maxExp = 250,
    interest = "외국어 공부",
)

private val previewMissions = listOf(
    MissionItem(1, "영어 회화 표현 5개 외우기", "출퇴근길에 소리 내어 따라 해보세요.", MissionDifficulty.MEDIUM, 100),
    MissionItem(2, "영어 뉴스 기사 하나 읽기", null, MissionDifficulty.HIGH, 200),
    MissionItem(3, "단어장 앱 10분 학습하기", null, MissionDifficulty.LOW, 50),
)

@Preview(showBackground = true, device = "id:pixel_7", name = "미션 있음")
@Composable
private fun HomePreview() {
    HaruUpTheme {
        HomeContent(
            uiState = HomeUiState(
                memberInfo = previewMemberInfo,
                challengeDay = 5,
                todayMissions = previewMissions,
                completedMissionIds = setOf(3),
            ),
            onBubbleClick = {}, onInfoClick = {},
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "미션 없음")
@Composable
private fun HomeEmptyPreview() {
    HaruUpTheme {
        HomeContent(
            uiState = HomeUiState(memberInfo = previewMemberInfo, isTooltipVisible = true),
            onBubbleClick = {}, onInfoClick = {},
        )
    }
}
