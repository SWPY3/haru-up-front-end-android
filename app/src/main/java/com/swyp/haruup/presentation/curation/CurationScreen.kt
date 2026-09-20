package com.swyp.haruup.presentation.curation

import androidx.compose.runtime.Composable
import com.swyp.haruup.core.component.PlaceholderScreen

/**
 * 큐레이션의 아직 구현하지 않은 단계를 대신하는 자리표시자입니다.
 * 남은 단계: 캐릭터 선택 완료 → 성격 선택 → 큐레이션 챗봇 → 오늘의 미션 선택
 *
 * @param curationSummary 단계 간 데이터 공유가 동작하는지 눈으로 확인하기 위한 임시 표시입니다.
 */
@Composable
fun CurationScreen(
    onCompleted: () -> Unit,
    curationSummary: String = "",
) {
    PlaceholderScreen(
        title = "큐레이션 — 남은 단계\n$curationSummary",
        buttonText = "완료",
        onButtonClick = onCompleted,
    )
}
