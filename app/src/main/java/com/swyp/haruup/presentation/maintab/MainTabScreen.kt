package com.swyp.haruup.presentation.maintab

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    onSignedOut: () -> Unit,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            MainTabBar(
                selectedTab = MainTab.entries.firstOrNull { it.route == currentRoute },
                onTabClick = { tab ->
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TabRoute.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TabRoute.HOME) { HomeScreen() }
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
