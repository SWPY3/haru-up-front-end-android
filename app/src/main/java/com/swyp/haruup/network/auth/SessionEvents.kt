package com.swyp.haruup.network.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 네트워크 계층에서 화면 쪽으로 "다시 로그인해야 한다" 는 신호를 보냅니다.
 *
 * 재발급은 OkHttp 스레드에서 일어나 화면을 직접 바꿀 수 없어서 이벤트로 넘깁니다.
 */
@Singleton
class SessionEvents @Inject constructor() {

    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** 재발급까지 실패해 로그인이 풀렸을 때 */
    val expired: SharedFlow<Unit> = _expired.asSharedFlow()

    fun notifyExpired() {
        _expired.tryEmit(Unit)
    }
}
