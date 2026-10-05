package com.swyp.haruup.presentation.mypage

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 마이페이지의 표시 문구와 메뉴 구성을 고정합니다.
 *
 * 메뉴 순서와 바깥 링크 주소는 iOS 에서 옮겨 온 값이라
 * 조용히 바뀌면 두 앱이 서로 다른 곳으로 가게 됩니다.
 */
class MyPageTest {

    // MARK: - 표시 문구

    @Test
    fun `닉네임이 있으면 님을 붙인다`() {
        assertEquals("하루나루님", MyPageUiState(nickname = "하루나루").displayName)
    }

    @Test
    fun `닉네임을 못 받아오면 사용자로 둔다`() {
        // 로그인 전이거나 프로필 조회가 실패한 경우입니다. (iOS 와 같은 기본값)
        assertEquals("사용자", MyPageUiState(nickname = null).displayName)
    }

    @Test
    fun `버전은 앱 버전을 그대로 쓴다`() {
        // iOS 는 문자열을 적어 두어 실제 버전과 어긋나 있습니다. 안드로이드는 빌드 값을 씁니다.
        val versionText = MyPageUiState().versionText

        assertTrue(versionText, versionText.startsWith("버전.v."))
        assertTrue(versionText, versionText.removePrefix("버전.v.").isNotBlank())
    }

    @Test
    fun `캐릭터 기본값은 하루다`() {
        assertEquals(1, MyPageUiState().characterId)
    }

    // MARK: - 메뉴 구성

    @Test
    fun `메뉴 순서는 iOS 와 같다`() {
        assertEquals(
            listOf("의견남기기", "문의하기", "알림 설정", "서비스 이용약관", "개인정보 처리방침", "로그아웃", "탈퇴하기"),
            MyPageMenu.entries.map { it.title },
        )
    }

    @Test
    fun `바깥 링크로 여는 메뉴만 주소를 가진다`() {
        val withUrl = MyPageMenu.entries.filter { it.url != null }

        assertEquals(
            listOf(MyPageMenu.FEEDBACK, MyPageMenu.INQUIRY, MyPageMenu.TERMS, MyPageMenu.PRIVACY_POLICY),
            withUrl,
        )
        assertTrue(withUrl.all { it.url!!.startsWith("https://") })
    }

    @Test
    fun `앱 안에서 처리하는 메뉴는 주소가 없다`() {
        assertNull(MyPageMenu.NOTIFICATION_SETTING.url)
        assertNull(MyPageMenu.LOGOUT.url)
        assertNull(MyPageMenu.WITHDRAW.url)
    }

    @Test
    fun `로그아웃과 탈퇴에는 화살표를 두지 않는다`() {
        assertFalse(MyPageMenu.LOGOUT.hasArrow)
        assertFalse(MyPageMenu.WITHDRAW.hasArrow)

        val others = MyPageMenu.entries - MyPageMenu.LOGOUT - MyPageMenu.WITHDRAW
        assertTrue(others.all { it.hasArrow })
    }

    @Test
    fun `탈퇴하기만 흐리게 보인다`() {
        assertEquals(listOf(MyPageMenu.WITHDRAW), MyPageMenu.entries.filter { it.isDestructive })
    }

    @Test
    fun `관심사 수정은 메뉴에 없다`() {
        // iOS 에 있지만 isHidden 으로 가려 둔 메뉴라 옮기지 않았습니다.
        assertTrue(MyPageMenu.entries.none { it.title == "관심사 수정" })
    }
}
