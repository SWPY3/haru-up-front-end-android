package com.swyp.haruup.presentation.navigation

/**
 * iOS 의 AppCoordinator / 각 Coordinator 가 담당하던 화면 전환 경로입니다.
 *
 * 앱 실행
 *   └ 스플래시
 * 로그인 미완료
 *   └ 로그인 → 약관 동의 → 온보딩 → 큐레이션(9단계)
 * 로그인 완료
 *   └ 메인 탭 (홈 / 기록 / 차트 / 마이페이지)
 */
object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val AGREE = "agree"
    const val ONBOARDING = "onboarding"
    const val CURATION = "curation"
    const val MAIN_TAB = "main_tab"
}

/** 메인 탭 하위 경로 */
object TabRoute {
    const val HOME = "home"
    const val HISTORY = "history"
    const val CHART = "chart"
    const val MY_PAGE = "my_page"
}
