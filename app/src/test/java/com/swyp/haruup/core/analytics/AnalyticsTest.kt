package com.swyp.haruup.core.analytics

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 이벤트 이름이 iOS 와 글자까지 같은지 확인합니다.
 * 한 글자라도 다르면 Amplitude 에서 두 앱의 데이터가 다른 이벤트로 갈라집니다.
 */
class AnalyticsTest {

    @After
    fun tearDown() {
        Analytics.sink = { _, _ -> }
    }

    /** iOS AnalyticsManager.swift 의 AppEvent 에서 화면에서 쓰이는 것만 옮겨 적은 목록 */
    private val iosEventNames = listOf(
        "[Login] Kakao Login Tapped", "[Login] Naver Login Tapped",
        "[Agree] Back Tapped", "[Agree] All Check Tapped",
        "[Agree] Term1 Check Tapped", "[Agree] Term2 Check Tapped", "[Agree] Term3 Check Tapped",
        "[Agree] Term1 Detail Tapped", "[Agree] Term2 Detail Tapped", "[Agree] Confirm Tapped",
        "[Onboarding] Next Tapped", "[Onboarding] Swipe Page", "[Onboarding] Complete",
        "[CharacterSelect] Left Arrow Tapped", "[CharacterSelect] Right Arrow Tapped",
        "[CharacterSelect] Character Image Tapped", "[CharacterSelect] Next Tapped",
        "[CurationChat] Close Tapped", "[CurationChat] Message Sent", "[CurationChat] Suggestion Chip Tapped",
        "[CurationChat] Exit Confirm Tapped", "[CurationChat] Chat Completed",
        "[MissionStart] Start Recommendation Tapped",
        "[MissionList] Close Tapped", "[MissionList] Refresh Tapped", "[MissionList] Complete Tapped",
        "[MissionList] Selected Mission Difficulty",
        "[Tab] Home Tapped", "[Tab] Record Tapped", "[Tab] Chart Tapped", "[Tab] MyPage Tapped",
        "[Home] Streak Button Tapped", "[Home] Character Tapped", "[Home] Speech Bubble Tapped",
        "[Home] Today Mission Info Tapped", "[Home] Add Mission Tapped", "[Home] Mission Result Tapped",
        "[Home] Complete Mission Tapped", "[Home] Delete Mission Tapped", "[Home] Confirm Delete Tapped",
        "[Home] Cancel Delete Tapped",
        "[Record] Prev Month Tapped", "[Record] Next Month Tapped", "[Record] Date Cell Selected",
        "[MyPage] Edit Profile Tapped", "[MyPage] Edit Interests Tapped", "[MyPage] Leave Feedback Tapped",
        "[MyPage] Contact Us Tapped", "[MyPage] Notification Settings Tapped",
        "[MyPage] Terms of Service Tapped", "[MyPage] Privacy Policy Tapped",
        "[MyPage] Logout Tapped", "[MyPage] Logout Confirm Tapped", "[MyPage] Logout Cancel Tapped",
        "[MyPage] Withdraw Tapped", "[MyPage] Withdraw Confirm Tapped", "[MyPage] Withdraw Cancel Tapped",
    )

    private val androidEventNames: List<String> = listOf(
        AppEvent.Login.entries, AppEvent.Agree.entries, AppEvent.Onboarding.entries,
        AppEvent.CharacterSelect.entries, AppEvent.CurationChat.entries, AppEvent.MissionStart.entries,
        AppEvent.MissionList.entries, AppEvent.Tab.entries, AppEvent.Home.entries,
        AppEvent.Record.entries, AppEvent.MyPage.entries,
    ).flatten().map { it.eventName }

    @Test
    fun `이벤트 이름이 iOS 와 같다`() {
        assertEquals(iosEventNames.sorted(), androidEventNames.sorted())
    }

    @Test
    fun `이벤트 이름과 속성을 그대로 넘긴다`() {
        val sent = mutableListOf<Pair<String, Map<String, Any>?>>()
        Analytics.sink = { name, properties -> sent += name to properties }

        Analytics.track(AppEvent.Onboarding.NEXT_TAPPED, mapOf("page" to 1))

        assertEquals(listOf("[Onboarding] Next Tapped" to mapOf("page" to 1)), sent)
    }
}
