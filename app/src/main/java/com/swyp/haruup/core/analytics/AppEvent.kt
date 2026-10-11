package com.swyp.haruup.core.analytics

/**
 * Amplitude 로 보내는 이벤트. iOS 의 AppEvent 와 이름(문자열)까지 같습니다.
 * 두 앱의 데이터를 한 차트에서 같이 보려면 글자 하나도 달라지면 안 됩니다.
 *
 * iOS 에 정의만 있고 화면에서 쓰이지 않는 이벤트(Apple 로그인, 온보딩 건너뛰기,
 * 미션 목록 info 아이콘)는 옮기지 않았습니다.
 */
sealed interface AppEvent {
    val eventName: String

    enum class Login(override val eventName: String) : AppEvent {
        KAKAO_LOGIN_TAPPED("[Login] Kakao Login Tapped"),
        NAVER_LOGIN_TAPPED("[Login] Naver Login Tapped"),
    }

    enum class Agree(override val eventName: String) : AppEvent {
        BACK_TAPPED("[Agree] Back Tapped"),
        ALL_CHECK_TAPPED("[Agree] All Check Tapped"),
        TERM1_CHECK_TAPPED("[Agree] Term1 Check Tapped"),
        TERM2_CHECK_TAPPED("[Agree] Term2 Check Tapped"),
        TERM3_CHECK_TAPPED("[Agree] Term3 Check Tapped"),
        TERM1_DETAIL_TAPPED("[Agree] Term1 Detail Tapped"),
        TERM2_DETAIL_TAPPED("[Agree] Term2 Detail Tapped"),
        CONFIRM_TAPPED("[Agree] Confirm Tapped"),
    }

    enum class Onboarding(override val eventName: String) : AppEvent {
        /** property: page (0 부터) */
        NEXT_TAPPED("[Onboarding] Next Tapped"),

        /** property: page (0 부터) */
        SWIPE_PAGE("[Onboarding] Swipe Page"),
        COMPLETE("[Onboarding] Complete"),
    }

    enum class CharacterSelect(override val eventName: String) : AppEvent {
        LEFT_ARROW_TAPPED("[CharacterSelect] Left Arrow Tapped"),
        RIGHT_ARROW_TAPPED("[CharacterSelect] Right Arrow Tapped"),
        CHARACTER_IMAGE_TAPPED("[CharacterSelect] Character Image Tapped"),

        /** property: character (캐릭터 이름) */
        NEXT_TAPPED("[CharacterSelect] Next Tapped"),
    }

    enum class CurationChat(override val eventName: String) : AppEvent {
        CLOSE_TAPPED("[CurationChat] Close Tapped"),
        MESSAGE_SENT("[CurationChat] Message Sent"),

        /** property: chip (칩 문구) */
        SUGGESTION_CHIP_TAPPED("[CurationChat] Suggestion Chip Tapped"),
        EXIT_CONFIRM_TAPPED("[CurationChat] Exit Confirm Tapped"),
        CHAT_COMPLETED("[CurationChat] Chat Completed"),
    }

    enum class MissionStart(override val eventName: String) : AppEvent {
        START_TAPPED("[MissionStart] Start Recommendation Tapped"),
    }

    enum class MissionList(override val eventName: String) : AppEvent {
        CLOSE_TAPPED("[MissionList] Close Tapped"),
        REFRESH_TAPPED("[MissionList] Refresh Tapped"),
        COMPLETE_TAPPED("[MissionList] Complete Tapped"),

        /** property: difficulty (서버 난이도 숫자). 고른 미션마다 한 번씩 보냅니다. */
        SELECTED_MISSION_DIFFICULTY("[MissionList] Selected Mission Difficulty"),
    }

    enum class Tab(override val eventName: String) : AppEvent {
        HOME_TAPPED("[Tab] Home Tapped"),
        RECORD_TAPPED("[Tab] Record Tapped"),
        CHART_TAPPED("[Tab] Chart Tapped"),
        MY_PAGE_TAPPED("[Tab] MyPage Tapped"),
    }

    enum class Home(override val eventName: String) : AppEvent {
        STREAK_BUTTON_TAPPED("[Home] Streak Button Tapped"),
        CHARACTER_TAPPED("[Home] Character Tapped"),
        SPEECH_BUBBLE_TAPPED("[Home] Speech Bubble Tapped"),
        TODAY_MISSION_INFO_TAPPED("[Home] Today Mission Info Tapped"),
        ADD_MISSION_TAPPED("[Home] Add Mission Tapped"),
        MISSION_RESULT_TAPPED("[Home] Mission Result Tapped"),
        COMPLETE_MISSION_TAPPED("[Home] Complete Mission Tapped"),
        DELETE_MISSION_TAPPED("[Home] Delete Mission Tapped"),
        CONFIRM_DELETE_TAPPED("[Home] Confirm Delete Tapped"),
        CANCEL_DELETE_TAPPED("[Home] Cancel Delete Tapped"),
    }

    enum class Record(override val eventName: String) : AppEvent {
        PREV_MONTH_TAPPED("[Record] Prev Month Tapped"),
        NEXT_MONTH_TAPPED("[Record] Next Month Tapped"),
        DATE_CELL_SELECTED("[Record] Date Cell Selected"),
    }

    enum class MyPage(override val eventName: String) : AppEvent {
        EDIT_PROFILE_TAPPED("[MyPage] Edit Profile Tapped"),
        EDIT_INTERESTS_TAPPED("[MyPage] Edit Interests Tapped"),
        LEAVE_FEEDBACK_TAPPED("[MyPage] Leave Feedback Tapped"),
        CONTACT_US_TAPPED("[MyPage] Contact Us Tapped"),
        NOTIFICATION_SETTINGS_TAPPED("[MyPage] Notification Settings Tapped"),
        TERMS_OF_SERVICE_TAPPED("[MyPage] Terms of Service Tapped"),
        PRIVACY_POLICY_TAPPED("[MyPage] Privacy Policy Tapped"),
        LOGOUT_TAPPED("[MyPage] Logout Tapped"),
        LOGOUT_CONFIRM_TAPPED("[MyPage] Logout Confirm Tapped"),
        LOGOUT_CANCEL_TAPPED("[MyPage] Logout Cancel Tapped"),
        WITHDRAW_TAPPED("[MyPage] Withdraw Tapped"),
        WITHDRAW_CONFIRM_TAPPED("[MyPage] Withdraw Confirm Tapped"),
        WITHDRAW_CANCEL_TAPPED("[MyPage] Withdraw Cancel Tapped"),
    }
}
