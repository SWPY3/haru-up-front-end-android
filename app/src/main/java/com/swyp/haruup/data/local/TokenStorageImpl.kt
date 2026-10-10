package com.swyp.haruup.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "haruup_prefs")

/**
 * DataStore 로 저장합니다.
 * 민감도가 더 높아지면 androidx.security:security-crypto 로 교체를 검토합니다.
 */
@Singleton
class TokenStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : TokenStorage {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    private val memberIdKey = stringPreferencesKey("member_id")

    /** 온보딩을 마친 회원이 누구였는지. 계정이 바뀌면 온보딩을 다시 시켜야 해서 함께 둡니다. */
    private val onboardingMemberIdKey = stringPreferencesKey("onboarding_member_id")

    override suspend fun getAccessToken(): String? =
        context.dataStore.data.first()[accessTokenKey]

    override suspend fun getRefreshToken(): String? =
        context.dataStore.data.first()[refreshTokenKey]

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            prefs[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun getMemberId(): String? = context.dataStore.data.first()[memberIdKey]

    override suspend fun saveMemberId(memberId: String) {
        context.dataStore.edit { it[memberIdKey] = memberId }
    }

    /**
     * 지난번과 다른 계정으로 로그인했다면 온보딩 기록을 지웁니다.
     *
     * 지우지 않으면 새 계정이 큐레이션을 건너뛰고 빈 홈으로 들어갑니다. (iOS 와 같은 처리)
     */
    override suspend fun clearOnboardingIfDifferentUser(memberId: String) {
        val previous = context.dataStore.data.first()[onboardingMemberIdKey] ?: return
        if (previous == memberId) return

        context.dataStore.edit { prefs ->
            prefs.remove(onboardingCompletedKey)
            prefs.remove(onboardingMemberIdKey)
        }
    }

    /**
     * 로그인된 상태인지 봅니다.
     *
     * iOS 는 만료 시각도 함께 보지만, 안드로이드는 아직 만료 시각을 저장하지 않습니다.
     * 그래서 토큰이 있는지만 확인합니다. 만료된 토큰은 401 을 받고 걸러집니다.
     * TODO: 토큰 만료 시각을 저장해 여기서도 확인하기 (AuthInterceptor 의 재발급 TODO 와 함께)
     */
    override suspend fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    /** 큐레이션까지 마쳤는지 여부입니다. 마치지 않았으면 약관 동의부터 다시 시작합니다. */
    override suspend fun isOnboardingCompleted(): Boolean =
        context.dataStore.data.first()[onboardingCompletedKey] == true

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[onboardingCompletedKey] = completed

            // 누가 마쳤는지 함께 적어 둡니다. 다음 로그인 때 계정이 바뀌었는지 보려면 필요합니다.
            val memberId = prefs[memberIdKey]
            if (completed && memberId != null) prefs[onboardingMemberIdKey] = memberId
        }
    }

    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
