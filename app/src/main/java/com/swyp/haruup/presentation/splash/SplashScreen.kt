package com.swyp.haruup.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme

private val LOGO_TO_NAME = 20.dp

/**
 * 앱을 열면 가장 먼저 보이는 화면. iOS 의 SplashViewController 에 대응합니다.
 *
 * 저장된 토큰을 보고 로그인 / 약관 동의 / 메인 탭 중 한 곳으로 보냅니다.
 */
@Composable
fun SplashScreen(
    onNeedLogin: () -> Unit,
    onOnboardingRequired: () -> Unit,
    onOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val result by viewModel.result.collectAsStateWithLifecycle()

    LaunchedEffect(result) {
        when (result) {
            SplashResult.NEED_LOGIN -> onNeedLogin()
            SplashResult.ONBOARDING_REQUIRED -> onOnboardingRequired()
            SplashResult.ONBOARDING_COMPLETED -> onOnboardingCompleted()
            null -> Unit
        }
    }

    SplashContent(modifier = modifier)
}

@Composable
private fun SplashContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(HaruUpColor.Gradient.SplashStart, HaruUpColor.Gradient.SplashEnd)
                )
            ),
        verticalArrangement = Arrangement.spacedBy(LOGO_TO_NAME, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.image_splash_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
        )

        Image(
            painter = painterResource(R.drawable.image_splash_app_name),
            contentDescription = "하루업",
            contentScale = ContentScale.Fit,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun SplashPreview() {
    HaruUpTheme { SplashContent() }
}
