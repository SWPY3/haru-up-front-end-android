package com.swyp.haruup.data.push

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 알림을 눌러 들어왔을 때 화면 쪽에 갈 곳을 알립니다. iOS 의 PushNotificationService.pushData 에 대응합니다.
 *
 * iOS 는 data 의 type 이 "home" 이면 홈 탭으로 보냅니다. 같은 값만 다룹니다.
 */
@Singleton
class PushEvents @Inject constructor() {

    private val _openHome = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)

    /**
     * 홈 탭으로 가라는 신호. 앱이 막 켜져 메인 탭이 아직 없을 수 있어 마지막 하나를 남겨 둡니다.
     * 받는 쪽은 처리한 뒤 [consumeOpenHome] 으로 비웁니다.
     */
    val openHome: SharedFlow<Unit> = _openHome.asSharedFlow()

    /** 알림 data 를 보고 갈 곳이 있으면 알립니다. */
    fun onNotificationOpened(type: String?) {
        if (type == TYPE_HOME) _openHome.tryEmit(Unit)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun consumeOpenHome() {
        _openHome.resetReplayCache()
    }

    companion object {
        /** 알림 data 에서 갈 곳을 담는 키 */
        const val KEY_TYPE = "type"
        const val TYPE_HOME = "home"
    }
}
