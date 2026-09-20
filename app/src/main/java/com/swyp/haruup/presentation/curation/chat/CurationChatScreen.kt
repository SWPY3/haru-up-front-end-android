package com.swyp.haruup.presentation.curation.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.presentation.curation.character.CharacterMate
import com.swyp.haruup.presentation.curation.chat.component.BotMessageBubble
import com.swyp.haruup.presentation.curation.chat.component.ChatInputBar
import com.swyp.haruup.presentation.curation.chat.component.SuggestionChips
import com.swyp.haruup.presentation.curation.chat.component.UserMessageBubble

private val CLOSE_BUTTON_SIZE = 32.dp
private val CLOSE_TOP_MARGIN = 10.dp
private val CLOSE_LEADING_MARGIN = 16.dp
private val CLOSE_TO_PROGRESS = 12.dp
private val PROGRESS_H_MARGIN = 20.dp
private val PROGRESS_HEIGHT = 4.dp
private val PROGRESS_TO_LIST = 12.dp
private val AVATAR_SIZE = 48.dp
private val AVATAR_TOP_MARGIN = 8.dp
private val AVATAR_BOTTOM_MARGIN = 8.dp

/**
 * 큐레이션 4단계. iOS 의 CurationChatViewController 에 대응합니다.
 *
 * 이번 단계에서는 대화 UI 만 구성했습니다.
 * 세션 관리와 API 연동, 종료 확인 모달은 이어서 붙입니다.
 */
@Composable
fun CurationChatScreen(
    characterId: Int,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CurationChatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.onStart() }

    CurationChatContent(
        uiState = uiState,
        characterId = characterId,
        onCloseClick = onCloseClick,
        onInputChange = viewModel::onInputChange,
        onSendClick = viewModel::onSendClick,
        onSuggestionClick = viewModel::onSuggestionClick,
        modifier = modifier,
    )
}

@Composable
private fun CurationChatContent(
    uiState: CurationChatUiState,
    characterId: Int,
    onCloseClick: () -> Unit,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mate = CharacterMate.entries.firstOrNull { it.id == characterId } ?: CharacterMate.HARU
    val listState = rememberLazyListState()

    // 새 메시지가 붙으면 항상 마지막으로 스크롤합니다.
    LaunchedEffect(uiState.displayItems.size) {
        if (uiState.displayItems.isNotEmpty()) {
            listState.animateScrollToItem(uiState.displayItems.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.Neutral10)
            .statusBarsPadding()
            .imePadding(),
    ) {
        Spacer(Modifier.height(CLOSE_TOP_MARGIN))

        Image(
            painter = painterResource(R.drawable.ic_xmark),
            contentDescription = "큐레이션 종료",
            modifier = Modifier
                .padding(start = CLOSE_LEADING_MARGIN)
                .size(CLOSE_BUTTON_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCloseClick,
                ),
        )

        Spacer(Modifier.height(CLOSE_TO_PROGRESS))

        LinearProgressIndicator(
            progress = { uiState.progress },
            color = HaruUpColor.Cta,
            trackColor = HaruUpColor.Neutral100,
            drawStopIndicator = {},
            gapSize = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PROGRESS_H_MARGIN)
                .height(PROGRESS_HEIGHT)
                .clip(RoundedCornerShape(PROGRESS_HEIGHT / 2)),
        )

        Spacer(Modifier.height(PROGRESS_TO_LIST))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            // iOS 는 테이블 헤더로 캐릭터 아바타를 둡니다.
            item(key = "avatar") {
                Image(
                    painter = painterResource(mate.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(
                            start = CLOSE_LEADING_MARGIN,
                            top = AVATAR_TOP_MARGIN,
                            bottom = AVATAR_BOTTOM_MARGIN,
                        )
                        .size(AVATAR_SIZE)
                        .clip(CircleShape)
                        .background(HaruUpColor.PrimaryBlue50, CircleShape),
                )
            }

            items(
                count = uiState.displayItems.size,
                key = { uiState.displayItems[it].key },
            ) { index ->
                when (val item = uiState.displayItems[index]) {
                    is ChatDisplayItem.Bot -> BotMessageBubble(item.message)
                    is ChatDisplayItem.User -> UserMessageBubble(item.message)
                    is ChatDisplayItem.SuggestionChips ->
                        SuggestionChips(item.suggestions, onSuggestionClick)
                }
            }
        }

        ChatInputBar(
            text = uiState.inputText,
            onTextChange = onInputChange,
            onSendClick = onSendClick,
            placeholder = uiState.inputPlaceholder,
            enabled = !uiState.isLoading,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun CurationChatPreview() {
    HaruUpTheme {
        CurationChatContent(
            uiState = CurationChatUiState(
                displayItems = listOf(
                    ChatDisplayItem.Bot(
                        ChatMessage(
                            type = ChatMessageType.BOT,
                            text = "안녕하세요! 어떤 목표를 이루고 싶으신가요?",
                        )
                    ),
                    ChatDisplayItem.User(
                        ChatMessage(type = ChatMessageType.USER, text = "영어 실력을 키우고 싶어요")
                    ),
                    ChatDisplayItem.Bot(
                        ChatMessage(
                            type = ChatMessageType.BOT,
                            text = "좋아요! 하루에 어느 정도 투자할 수 있나요?",
                            subtitleText = "편하게 답해주세요",
                        )
                    ),
                    ChatDisplayItem.SuggestionChips(listOf("30분 이하", "1시간 정도", "2시간 이상")),
                ),
                progress = 0.4f,
            ),
            characterId = 1,
            onCloseClick = {}, onInputChange = {}, onSendClick = {}, onSuggestionClick = {},
        )
    }
}
