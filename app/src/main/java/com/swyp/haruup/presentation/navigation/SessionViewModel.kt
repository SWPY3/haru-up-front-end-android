package com.swyp.haruup.presentation.navigation

import androidx.lifecycle.ViewModel
import com.swyp.haruup.network.auth.SessionEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

/**
 * 로그인이 풀렸다는 신호를 화면 전환 쪽에 넘깁니다.
 * NavHost 가 Hilt 에서 직접 받을 수 없어 ViewModel 을 거칩니다.
 */
@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionEvents: SessionEvents,
) : ViewModel() {

    val sessionExpired: SharedFlow<Unit> = sessionEvents.expired
}
