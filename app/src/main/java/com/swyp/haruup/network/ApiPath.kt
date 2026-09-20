package com.swyp.haruup.network

/**
 * iOS 의 NetworkDefine 에 대응합니다.
 * baseURL 은 BuildConfig.BASE_URL 로 관리하므로 여기에는 path 만 둡니다.
 */
object ApiPath {

    object Auth {
        const val SNS_LOGIN = "api/member/auth/sns-login"
        const val LOGOUT = "api/member/auth/logout"
        const val WITHDRAW = "api/member/account/withdraw"
    }

    object Chatbot {
        const val START = "api/member/curation/chatbot/start"
        const val ANSWER = "api/member/curation/chatbot/answer"
        const val SETUP = "api/member/curation/chatbot-setup"
    }

    object Character {
        const val LIST = "api/character/list"
        const val PERSONALITY_LIST = "api/character/personality/list"
        const val SELECT_PERSONALITY = "api/character/personality"
    }

    object Profile {
        const val NICKNAME_DUPLICATE_CHECK = "api/member/profile/nickName_duplicate_check"
        const val PROFILE = "api/member/profile/profile"
    }

    // TODO: Mission / Member / Chart / Interests / Job / Notification / Ad 경로 이관
}
