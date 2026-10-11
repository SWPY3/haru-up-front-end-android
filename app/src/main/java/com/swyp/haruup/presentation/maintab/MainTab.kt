package com.swyp.haruup.presentation.maintab

import androidx.annotation.DrawableRes
import com.swyp.haruup.R
import com.swyp.haruup.presentation.navigation.TabRoute

/**
 * 메인 탭 네 개. iOS 의 MainTab 과 순서·이름이 같습니다.
 */
enum class MainTab(
    val route: String,
    val title: String,
    @DrawableRes val unselectedIcon: Int,
    @DrawableRes val selectedIcon: Int,
) {
    HOME(
        route = TabRoute.HOME,
        title = "홈",
        unselectedIcon = R.drawable.ic_tab_home_unselected,
        selectedIcon = R.drawable.ic_tab_home_selected,
    ),
    HISTORY(
        route = TabRoute.HISTORY,
        title = "나의 기록",
        unselectedIcon = R.drawable.ic_tab_history_unselected,
        selectedIcon = R.drawable.ic_tab_history_selected,
    ),
    CHART(
        route = TabRoute.CHART,
        title = "차트",
        unselectedIcon = R.drawable.ic_tab_chart_unselected,
        selectedIcon = R.drawable.ic_tab_chart_selected,
    ),
    MY_PAGE(
        route = TabRoute.MY_PAGE,
        title = "마이페이지",
        unselectedIcon = R.drawable.ic_tab_mypage_unselected,
        selectedIcon = R.drawable.ic_tab_mypage_selected,
    ),
}
