package com.swyp.haruup.data.push

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** 알림을 눌렀을 때 홈으로 보내는 신호를 확인합니다. iOS 는 type 이 "home" 일 때만 보냅니다. */
class PushEventsTest {

    @Test
    fun `type 이 home 이면 홈으로 보낸다`() {
        val events = PushEvents()

        events.onNotificationOpened("home")

        assertEquals(1, events.openHome.replayCache.size)
    }

    @Test
    fun `type 이 없거나 모르는 값이면 아무 데도 보내지 않는다`() {
        val events = PushEvents()

        events.onNotificationOpened(null)
        events.onNotificationOpened("chart")

        assertTrue(events.openHome.replayCache.isEmpty())
    }

    @Test
    fun `처리한 뒤에는 다시 보내지 않는다`() {
        // 남겨 두면 화면이 다시 만들어질 때마다 홈으로 튕깁니다.
        val events = PushEvents().apply { onNotificationOpened("home") }

        events.consumeOpenHome()

        assertTrue(events.openHome.replayCache.isEmpty())
    }
}
