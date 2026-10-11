package com.swyp.haruup.data.local

/**
 * 토큰과 로그인 관련 로컬 저장. iOS 의 TokenStorageService 에 대응합니다.
 *
 * 저장 방식을 감추려고 인터페이스로 두었습니다.
 * ViewModel 이 Context 없이 테스트될 수 있어야 하기 때문이기도 합니다.
 */
interface TokenStorage {

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun saveTokens(accessToken: String, refreshToken: String)

    /**
     * 토큰만 지웁니다. 재발급까지 실패해 다시 로그인해야 할 때 씁니다.
     * 온보딩 기록은 남겨 두어 같은 계정으로 다시 로그인하면 큐레이션을 건너뜁니다.
     */
    suspend fun clearTokens()

    suspend fun getMemberId(): String?

    suspend fun saveMemberId(memberId: String)

    /**
     * 지난번과 다른 계정으로 로그인했다면 온보딩 기록을 지웁니다.
     * 지우지 않으면 새 계정이 큐레이션을 건너뛰고 빈 홈으로 들어갑니다.
     */
    suspend fun clearOnboardingIfDifferentUser(memberId: String)

    /** 로그인된 상태인지 봅니다. */
    suspend fun isLoggedIn(): Boolean

    /** 큐레이션까지 마쳤는지 여부입니다. */
    suspend fun isOnboardingCompleted(): Boolean

    suspend fun setOnboardingCompleted(completed: Boolean)

    /** 미션 고르기 소개 화면을 마지막으로 보여 준 날(yyyy-MM-dd). 하루 한 번만 보여 주려고 둡니다. */
    suspend fun getLastMissionIntroDate(): String?

    suspend fun setLastMissionIntroDate(date: String)

    /** 서버에 마지막으로 등록한 푸시 토큰. 토큰이 바뀌었을 때만 다시 보내려고 둡니다. */
    suspend fun getSentPushToken(): String?

    suspend fun setSentPushToken(token: String)

    suspend fun clear()
}
