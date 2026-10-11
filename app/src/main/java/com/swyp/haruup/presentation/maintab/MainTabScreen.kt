package com.swyp.haruup.presentation.maintab

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swyp.haruup.core.analytics.Analytics
import com.swyp.haruup.presentation.chart.ChartScreen
import com.swyp.haruup.presentation.history.HistoryScreen
import com.swyp.haruup.presentation.home.HomeScreen
import com.swyp.haruup.presentation.maintab.component.MainTabBar
import com.swyp.haruup.presentation.mypage.MyPageScreen
import com.swyp.haruup.presentation.navigation.TabRoute

@Composable
fun MainTabScreen(
    onEditProfileClick: () -> Unit,
    onNotificationSettingClick: () -> Unit,
    onSelectMissionClick: (fixedIds: List<Int>) -> Unit,
    onSignedOut: () -> Unit,
    viewModel: MainTabViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    fun selectTab(tab: MainTab) {
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // 알림을 눌러 들어오면 홈 탭으로 보냅니다. (iOS AppCoordinator.navigateToHome)
    LaunchedEffect(viewModel) {
        viewModel.openHome.collect {
            selectTab(MainTab.HOME)
            viewModel.onOpenHomeHandled()
        }
    }

    Scaffold(
        bottomBar = {
            MainTabBar(
                selectedTab = MainTab.entries.firstOrNull { it.route == currentRoute },
                onTabClick = { tab ->
                    Analytics.track(tab.analyticsEvent)
                    selectTab(tab)
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TabRoute.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TabRoute.HOME) { HomeScreen(onSelectMissionClick = onSelectMissionClick) }
            composable(TabRoute.HISTORY) { HistoryScreen() }
            composable(TabRoute.CHART) { ChartScreen() }
            composable(TabRoute.MY_PAGE) {
                MyPageScreen(
                    onEditProfileClick = onEditProfileClick,
                    onNotificationSettingClick = onNotificationSettingClick,
                    onSignedOut = onSignedOut,
                )
            }
        }
    }
}
