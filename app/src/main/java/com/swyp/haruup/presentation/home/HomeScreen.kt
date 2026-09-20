package com.swyp.haruup.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.presentation.home.component.HomeHeader

/**
 * 메인 탭의 홈. iOS 의 HomeViewController 에 대응합니다.
 *
 * 이번 단계에서는 상단 영역만 구성했습니다.
 * 오늘의 미션 목록과 바텀시트는 이어서 붙입니다.
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
        modifier = modifier,
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onBubbleClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10),
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
        }

        // TODO: 오늘의 미션 섹션 추가
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "레벨 3")
@Composable
private fun HomePreview() {
    HaruUpTheme {
        HomeContent(
            uiState = HomeUiState(
                memberInfo = HomeMemberInfo(
                    characterId = 1,
                    level = 3,
                    nickname = "영현",
                    currentExp = 50,
                    maxExp = 250,
                    interest = "외국어 공부",
                ),
                challengeDay = 5,
            ),
            onBubbleClick = {},
        )
    }
}
