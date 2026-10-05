package com.swyp.haruup.presentation.splash

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 앱을 열었을 때 어디로 가는지를 고정합니다.
 * 여기가 틀리면 로그인한 사용자가 로그인 화면으로 돌아가거나,
 * 큐레이션을 안 한 사용자가 빈 홈을 보게 됩니다.
 */
class SplashTest {

    @Test
    fun `로그인 전이면 로그인 화면으로 간다`() {
        assertEquals(
            SplashResult.NEED_LOGIN,
            SplashViewModel.decide(isLoggedIn = false, isOnboardingCompleted = false),
        )
    }

    @Test
    fun `로그인 전이면 온보딩 기록이 남아 있어도 로그인 화면으로 간다`() {
        // 탈퇴나 로그아웃으로 토큰만 지워진 경우입니다.
        assertEquals(
            SplashResult.NEED_LOGIN,
            SplashViewModel.decide(isLoggedIn = false, isOnboardingCompleted = true),
        )
    }

    @Test
    fun `로그인은 했지만 큐레이션을 안 마쳤으면 약관 동의부터 간다`() {
        assertEquals(
            SplashResult.ONBOARDING_REQUIRED,
            SplashViewModel.decide(isLoggedIn = true, isOnboardingCompleted = false),
        )
    }

    @Test
    fun `둘 다 마쳤으면 메인 탭으로 간다`() {
        assertEquals(
            SplashResult.ONBOARDING_COMPLETED,
            SplashViewModel.decide(isLoggedIn = true, isOnboardingCompleted = true),
        )
    }

    @Test
    fun `로고는 최소 1초 보여준다`() {
        assertEquals(1_000L, SplashViewModel.MIN_DISPLAY_MILLIS)
    }
}
