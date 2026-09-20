package com.swyp.haruup.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.presentation.login.component.SocialLoginButton

private val IMAGE_TO_BUTTON_SPACING = 36.dp
private val HORIZONTAL_MARGIN = 20.dp
private val BUTTON_SPACING = 16.dp
private val BOTTOM_MIN_MARGIN = 96.dp

/**
 * iOS 의 LoginViewController 에 대응합니다.
 *
 * 레이아웃 수치는 iOS 제약을 그대로 옮겼습니다.
 * - 배경 이미지: 상단 고정, 가로 꽉 채움, 비율 유지
 * - 버튼 스택: 이미지 아래 36, 좌우 여백 20, 버튼 간격 16, 하단 최소 여백 96
 */
@Composable
fun LoginScreen(
    onKakaoLoginClick: () -> Unit,
    onNaverLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HaruUpColor.AppWhite)
                .navigationBarsPadding(),
        ) {
            Image(
                painter = painterResource(R.drawable.image_login_background),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
            )

            Spacer(Modifier.height(IMAGE_TO_BUTTON_SPACING))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = HORIZONTAL_MARGIN),
                verticalArrangement = Arrangement.spacedBy(BUTTON_SPACING),
            ) {
                SocialLoginButton(
                    text = "카카오로 로그인",
                    iconRes = R.drawable.kakao_logo,
                    backgroundColor = HaruUpColor.Background.Kakao,
                    contentColor = HaruUpColor.AppBlack,
                    onClick = onKakaoLoginClick,
                    enabled = !isLoading,
                )

                SocialLoginButton(
                    text = "네이버로 로그인",
                    iconRes = R.drawable.naver_logo,
                    backgroundColor = HaruUpColor.Background.Naver,
                    contentColor = HaruUpColor.AppWhite,
                    onClick = onNaverLoginClick,
                    enabled = !isLoading,
                )
            }

            // iOS 는 버튼 스택 하단을 화면 바닥에서 최소 96 떨어뜨립니다.
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(BOTTOM_MIN_MARGIN))
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = HaruUpColor.PrimaryBlue700,
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun LoginScreenPreview() {
    HaruUpTheme {
        LoginScreen(onKakaoLoginClick = {}, onNaverLoginClick = {})
    }
}

@Preview(showBackground = true, device = "id:pixel_7", name = "로딩 중")
@Composable
private fun LoginScreenLoadingPreview() {
    HaruUpTheme {
        LoginScreen(onKakaoLoginClick = {}, onNaverLoginClick = {}, isLoading = true)
    }
}
