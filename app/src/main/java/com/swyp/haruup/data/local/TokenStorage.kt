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
 * iOS 의 Keychain / TokenStorageService 에 대응합니다.
 * 민감도가 더 높아지면 androidx.security:security-crypto 로 교체를 검토합니다.
 */
@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    suspend fun getAccessToken(): String? =
        context.dataStore.data.first()[accessTokenKey]

    suspend fun getRefreshToken(): String? =
        context.dataStore.data.first()[refreshTokenKey]

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            prefs[refreshTokenKey] = refreshToken
        }
    }

    /**
     * 로그인된 상태인지 봅니다.
     *
     * iOS 는 만료 시각도 함께 보지만, 안드로이드는 아직 만료 시각을 저장하지 않습니다.
     * 그래서 토큰이 있는지만 확인합니다. 만료된 토큰은 401 을 받고 걸러집니다.
     * TODO: 토큰 만료 시각을 저장해 여기서도 확인하기 (AuthInterceptor 의 재발급 TODO 와 함께)
     */
    suspend fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    /** 큐레이션까지 마쳤는지 여부입니다. 마치지 않았으면 약관 동의부터 다시 시작합니다. */
    suspend fun isOnboardingCompleted(): Boolean =
        context.dataStore.data.first()[onboardingCompletedKey] == true

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[onboardingCompletedKey] = completed }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
