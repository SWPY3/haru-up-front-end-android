package com.swyp.haruup.presentation.curation

import androidx.compose.runtime.Composable
import com.swyp.haruup.core.component.PlaceholderScreen

/**
 * iOS 의 Presentation/Curation 에 대응합니다.
 * 닉네임 → 생일 → 성별 → 직업 → 관심사 → 목표 순 9단계이며,
 * 단계별 화면을 이 패키지 하위에 추가하고 내부 NavHost 로 연결합니다.
 */
@Composable
fun CurationScreen(onCompleted: () -> Unit) {
    PlaceholderScreen(title = "큐레이션 (9단계)", buttonText = "완료", onButtonClick = onCompleted)
}
