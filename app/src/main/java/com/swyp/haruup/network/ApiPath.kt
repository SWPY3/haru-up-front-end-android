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

    object Ranking {
        const val POPULAR = "api/ranking/popular"
    }

    object Member {
        /** 홈 상단의 캐릭터 / 레벨 / 경험치. GET 이 아니라 POST 입니다. */
        const val HOME_INFO = "api/member/account/home/memberInfo"
    }

    object Mission {
        /** 오늘 고를 수 있는 미션 추천 */
        const val RECOMMEND = "api/member/mission/recommend"

        /** 다른 미션으로 다시 추천 */
        const val RETRY = "api/member/mission/retry"

        /** 고른 미션 확정 */
        const val SELECT = "api/member/mission/select"

        /** 날짜별 미션 목록 */
        const val LIST = "api/member/mission"

        /** 미션 완료 / 삭제 */
        const val STATUS = "api/member/mission/status"

        /** 날짜별 달성 여부 (연속 달성일 계산용) */
        const val COMPLETION_STATUS = "api/member/mission/completion-status"

        /**
         * 월별 집계. 뒤에 /{yyyy-MM} 을 붙이면 그 달의 일별 현황,
         * 기간을 쿼리로 주면 월별 달성일수가 나옵니다. (iOS 의 history / growth)
         */
        const val MONTHLY = "api/member/mission/continue/mission/month"
    }

    object Interest {
        /** 관심사 / 세부 관심사 / 목표를 모두 이 경로에서 parentId 로 구분해 받습니다. */
        const val DATA = "api/interests/data"

        /** 회원이 고른 관심사. 수정할 때는 뒤에 memberInterestId 를 붙입니다. */
        const val MEMBER = "api/interests/member"
    }

    // TODO: Job / Notification / Ad 경로 이관
}
