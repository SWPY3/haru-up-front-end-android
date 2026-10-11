package com.swyp.haruup.presentation.login

import com.swyp.haruup.data.model.SocialLoginProvider
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 로그인 뒤 분기와 안내 문구를 고정합니다.
 *
 * 앱 키가 아직 없어 SDK 를 거치는 경로는 돌려볼 수 없습니다.
 * 그 부분은 키가 들어온 뒤 기기에서 확인해야 합니다.
 */
class LoginViewModelTest {

    @Test
    fun `큐레이션을 마쳤으면 바로 메인 탭으로 간다`() {
        assertEquals(
            LoginDestination.MAIN_TAB,
            LoginViewModel.destinationFor(onboardingCompleted = true),
        )
    }

    @Test
    fun `큐레이션을 마치지 않았으면 약관 동의부터 간다`() {
        // 로그인만 하고 큐레이션을 건너뛰면 홈에 보여 줄 미션이 없습니다.
        assertEquals(
            LoginDestination.ONBOARDING,
            LoginViewModel.destinationFor(onboardingCompleted = false),
        )
    }

    @Test
    fun `앱 키가 없을 때의 안내에 어떤 로그인인지 담는다`() {
        assertEquals(
            "카카오 로그인이 아직 준비되지 않았어요.",
            LoginViewModel.notConfiguredMessage(SocialLoginProvider.KAKAO),
        )
        assertEquals(
            "네이버 로그인이 아직 준비되지 않았어요.",
            LoginViewModel.notConfiguredMessage(SocialLoginProvider.NAVER),
        )
    }
}
