package com.swyp.haruup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swyp.haruup.presentation.agree.AgreeScreen
import com.swyp.haruup.presentation.curation.CurationScreen
import com.swyp.haruup.presentation.login.LoginScreen
import com.swyp.haruup.presentation.maintab.MainTabScreen
import com.swyp.haruup.presentation.onboarding.OnboardingScreen
import com.swyp.haruup.presentation.splash.SplashScreen

@Composable
fun HaruUpNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.SPLASH) {

        composable(Route.SPLASH) {
            SplashScreen(
                onLoggedIn = {
                    navController.navigate(Route.MAIN_TAB) {
                        popUpTo(Route.SPLASH) { inclusive = true }
                    }
                },
                onLoggedOut = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(Route.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(Route.LOGIN) {
            // TODO: SDK 연동 후 실제 로그인 결과에 따라 이동하도록 교체
            LoginScreen(
                onKakaoLoginClick = { navController.navigate(Route.AGREE) },
                onNaverLoginClick = { navController.navigate(Route.AGREE) },
            )
        }

        composable(Route.AGREE) {
            AgreeScreen(onAgreed = { navController.navigate(Route.ONBOARDING) })
        }

        composable(Route.ONBOARDING) {
            OnboardingScreen(onFinished = { navController.navigate(Route.CURATION) })
        }

        composable(Route.CURATION) {
            CurationScreen(
                onCompleted = {
                    navController.navigate(Route.MAIN_TAB) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.MAIN_TAB) {
            MainTabScreen()
        }
    }
}
