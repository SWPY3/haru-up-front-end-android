package com.swyp.haruup.presentation.maintab

import com.swyp.haruup.presentation.navigation.TabRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * 탭의 순서와 이름을 고정합니다. iOS 의 MainTab 과 같아야 합니다.
 */
class MainTabTest {

    @Test
    fun `탭 순서는 iOS 와 같다`() {
        assertEquals(
            listOf("홈", "나의 기록", "차트", "마이페이지"),
            MainTab.entries.map { it.title },
        )
    }

    @Test
    fun `탭마다 경로가 다르다`() {
        val routes = MainTab.entries.map { it.route }

        assertEquals(routes.size, routes.toSet().size)
        assertEquals(TabRoute.HOME, MainTab.HOME.route)
        assertEquals(TabRoute.MY_PAGE, MainTab.MY_PAGE.route)
    }

    @Test
    fun `선택 아이콘과 미선택 아이콘이 다르다`() {
        MainTab.entries.forEach { tab ->
            assertNotEquals(tab.title, tab.selectedIcon, tab.unselectedIcon)
        }
    }
}
