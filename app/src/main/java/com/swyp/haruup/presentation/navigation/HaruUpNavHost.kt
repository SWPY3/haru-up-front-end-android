package com.swyp.haruup.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.swyp.haruup.presentation.agree.AgreeScreen
import com.swyp.haruup.presentation.curation.CurationScreen
import com.swyp.haruup.presentation.curation.character.CharacterSelectScreen
import com.swyp.haruup.presentation.curation.curationViewModel
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
            AgreeScreen(
                onBackClick = { navController.popBackStack() },
                onAgreed = { navController.navigate(Route.ONBOARDING) },
            )
        }

        composable(Route.ONBOARDING) {
            OnboardingScreen(onFinished = { navController.navigate(Route.CURATION_GRAPH) })
        }

        curationGraph(navController)

        composable(Route.MAIN_TAB) {
            MainTabScreen()
        }
    }
}

/**
 * 큐레이션 중첩 그래프입니다.
 *
 * 단계별 화면은 각자의 ViewModel 로 자기 화면 상태를 다루고,
 * 다음 단계로 넘길 값만 그래프에 스코프된 CurationViewModel 에 씁니다.
 * 그래프를 벗어나면 CurationViewModel 이 정리되므로 재진입 시 입력이 남지 않습니다.
 */
private fun NavGraphBuilder.curationGraph(navController: NavHostController) {
    navigation(startDestination = CurationRoute.CHARACTER, route = Route.CURATION_GRAPH) {

        composable(CurationRoute.CHARACTER) { entry ->
            val curationViewModel = entry.curationViewModel(navController)

            CharacterSelectScreen(
                onNextClick = { characterId ->
                    curationViewModel.setCharacterId(characterId)
                    navController.navigate(CurationRoute.CHARACTER_COMPLETE)
                },
            )
        }

        // TODO: ② 캐릭터 선택 완료 → ③ 성격 선택 → ④ 큐레이션 챗봇 순으로 교체
        composable(CurationRoute.CHARACTER_COMPLETE) { entry ->
            val curationViewModel = entry.curationViewModel(navController)
            val curationData by curationViewModel.curationData.collectAsStateWithLifecycle()

            CurationScreen(
                curationSummary = "선택한 캐릭터 ID: ${curationData.characterId ?: "없음"}",
                onCompleted = {
                    navController.navigate(Route.MAIN_TAB) {
                        popUpTo(Route.LOGIN) { inclusive = true }
                    }
                },
            )
        }
    }
}
