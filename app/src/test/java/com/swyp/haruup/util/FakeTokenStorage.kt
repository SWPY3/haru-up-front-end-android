package com.swyp.haruup.util

import com.swyp.haruup.data.local.TokenStorage

/** 테스트용 TokenStorage 입니다. 메모리에만 들고 있습니다. */
class FakeTokenStorage(
    private var accessToken: String? = null,
    private var refreshToken: String? = null,
    private var memberId: String? = null,
    private var onboardingCompleted: Boolean = false,
) : TokenStorage {

    /** 온보딩을 마친 회원. 계정이 바뀌었는지 보는 데 씁니다. */
    private var onboardingMemberId: String? = memberId?.takeIf { onboardingCompleted }

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun getRefreshToken(): String? = refreshToken

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    override suspend fun getMemberId(): String? = memberId

    override suspend fun saveMemberId(memberId: String) {
        this.memberId = memberId
    }

    override suspend fun clearOnboardingIfDifferentUser(memberId: String) {
        val previous = onboardingMemberId ?: return
        if (previous == memberId) return

        onboardingCompleted = false
        onboardingMemberId = null
    }

    override suspend fun isLoggedIn(): Boolean = !accessToken.isNullOrBlank()

    override suspend fun isOnboardingCompleted(): Boolean = onboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingCompleted = completed
        if (completed) onboardingMemberId = memberId
    }

    override suspend fun clear() {
        accessToken = null
        refreshToken = null
        memberId = null
        onboardingCompleted = false
        onboardingMemberId = null
    }
}
