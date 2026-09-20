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
 */
object Route {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val AGREE = "agree"
    const val ONBOARDING = "onboarding"
    const val MAIN_TAB = "main_tab"

    /**
     * 큐레이션 중첩 그래프.
     * CurationViewModel 이 이 경로의 BackStackEntry 에 스코프되어 단계 간 데이터를 공유합니다.
     */
    const val CURATION_GRAPH = "curation"
}

/**
 * 큐레이션 단계별 경로입니다.
 * iOS AppCoordinator.showCurationFlow 의 순서를 그대로 따릅니다.
 */
object CurationRoute {
    /** ① 캐릭터 선택 */
    const val CHARACTER = "curation/character"

    /** ② 캐릭터 선택 완료 */
    const val CHARACTER_COMPLETE = "curation/character_complete"

    /** ③ 성격 선택 */
    const val PERSONALITY = "curation/personality"

    /** ④ 큐레이션 챗봇 */
    const val CHAT = "curation/chat"

    /** 큐레이션 직후 오늘의 미션 선택 */
    const val TODAY_MISSION = "curation/today_mission"
}

/** 메인 탭 하위 경로 */
object TabRoute {
    const val HOME = "home"
    const val HISTORY = "history"
    const val CHART = "chart"
    const val MY_PAGE = "my_page"
}
