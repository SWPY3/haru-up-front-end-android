package com.swyp.haruup.presentation.navigation

/**
 * iOS 의 AppCoordinator / 각 Coordinator 가 담당하던 화면 전환 경로입니다.
 *
 * 앱 실행
 *   └ 스플래시
 * 로그인 미완료
 *   └ 로그인 → 약관 동의 → 온보딩 → 큐레이션
 * 로그인 완료
 *   └ 메인 탭 (홈 / 기록 / 차트 / 마이페이지)
 *
 * 큐레이션은 아래 순서입니다. (iOS AppCoordinator.showCurationFlow 기준)
 *   캐릭터 선택 → 캐릭터 선택 완료 → 성격 선택 → 큐레이션 챗봇 → 오늘의 미션 선택
 */
object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val AGREE = "agree"
    const val ONBOARDING = "onboarding"
    const val MAIN_TAB = "main_tab"

    /** 큐레이션 단계별 경로 */
    const val CURATION_CHARACTER = "curation/character"
    const val CURATION_REST = "curation/rest"
}

/** 메인 탭 하위 경로 */
object TabRoute {
    const val HOME = "home"
    const val HISTORY = "history"
    const val CHART = "chart"
    const val MY_PAGE = "my_page"
}
