package com.swyp.haruup.presentation.curation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.swyp.haruup.presentation.navigation.Route

/**
 * 큐레이션 중첩 그래프에 스코프된 [CurationViewModel] 을 가져옵니다.
 *
 * 각 단계 화면에서 그냥 hiltViewModel() 을 부르면 그 화면 전용 인스턴스가 생겨
 * 단계 간에 값이 공유되지 않습니다. 반드시 이 함수를 써야 합니다.
 */
@Composable
fun NavBackStackEntry.curationViewModel(navController: NavController): CurationViewModel {
    val graphEntry = remember(this) { navController.getBackStackEntry(Route.CURATION_GRAPH) }
    return hiltViewModel(graphEntry)
}
