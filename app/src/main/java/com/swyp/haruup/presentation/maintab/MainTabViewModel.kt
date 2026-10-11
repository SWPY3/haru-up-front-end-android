package com.swyp.haruup.presentation.maintab

import androidx.lifecycle.ViewModel
import com.swyp.haruup.data.push.PushEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

/** 알림을 눌러 들어왔을 때 홈 탭으로 보내는 신호를 탭 화면에 넘깁니다. */
@HiltViewModel
class MainTabViewModel @Inject constructor(
    private val pushEvents: PushEvents,
) : ViewModel() {

    val openHome: SharedFlow<Unit> = pushEvents.openHome

    fun onOpenHomeHandled() {
        pushEvents.consumeOpenHome()
    }
}
