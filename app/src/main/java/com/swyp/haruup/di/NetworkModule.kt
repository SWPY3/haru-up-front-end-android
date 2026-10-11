package com.swyp.haruup.di

import com.swyp.haruup.BuildConfig
import com.swyp.haruup.network.interceptor.AuthInterceptor
import com.swyp.haruup.network.service.AuthService
import com.swyp.haruup.network.service.CharacterService
import com.swyp.haruup.network.service.ChartService
import com.swyp.haruup.network.service.ChatbotService
import com.swyp.haruup.network.service.InterestService
import com.swyp.haruup.network.service.MemberService
import com.swyp.haruup.network.service.MissionService
import com.swyp.haruup.network.service.ProfileService
import com.swyp.haruup.network.service.TokenReissueService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        // 값이 없는 필드는 보내지 않습니다. iOS 가 nil 파라미터를 딕셔너리에서 빼는 것과 같습니다.
        explicitNulls = false
    }

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    /**
     * 토큰 재발급 전용 클라이언트. AuthInterceptor 를 붙이지 않습니다.
     * 붙이면 AuthInterceptor → TokenRefresher → 이 클라이언트 → AuthInterceptor 로 의존이 맴돕니다.
     */
    @Provides
    @Singleton
    @Named(REISSUE)
    fun provideReissueRetrofit(json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideTokenReissueService(@Named(REISSUE) retrofit: Retrofit): TokenReissueService =
        retrofit.create(TokenReissueService::class.java)

    private const val REISSUE = "reissue"

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideCharacterService(retrofit: Retrofit): CharacterService =
        retrofit.create(CharacterService::class.java)

    @Provides
    @Singleton
    fun provideChatbotService(retrofit: Retrofit): ChatbotService =
        retrofit.create(ChatbotService::class.java)

    @Provides
    @Singleton
    fun provideChartService(retrofit: Retrofit): ChartService =
        retrofit.create(ChartService::class.java)

    @Provides
    @Singleton
    fun provideProfileService(retrofit: Retrofit): ProfileService =
        retrofit.create(ProfileService::class.java)

    @Provides
    @Singleton
    fun provideInterestService(retrofit: Retrofit): InterestService =
        retrofit.create(InterestService::class.java)

    @Provides
    @Singleton
    fun provideMissionService(retrofit: Retrofit): MissionService =
        retrofit.create(MissionService::class.java)

    @Provides
    @Singleton
    fun provideMemberService(retrofit: Retrofit): MemberService =
        retrofit.create(MemberService::class.java)
}
