package com.swyp.haruup.presentation.maintab

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.swyp.haruup.presentation.chart.ChartScreen
import com.swyp.haruup.presentation.history.HistoryScreen
import com.swyp.haruup.presentation.home.HomeScreen
import com.swyp.haruup.presentation.mypage.MyPageScreen
import com.swyp.haruup.presentation.navigation.TabRoute

private data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

private val tabs = listOf(
    TabItem(TabRoute.HOME, "홈", Icons.Default.Home),
    TabItem(TabRoute.HISTORY, "기록", Icons.Default.CalendarMonth),
    TabItem(TabRoute.CHART, "차트", Icons.Default.BarChart),
    TabItem(TabRoute.MY_PAGE, "마이페이지", Icons.Default.Person),
)

@Composable
fun MainTabScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
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
            composable(TabRoute.MY_PAGE) { MyPageScreen() }
        }
    }
}
