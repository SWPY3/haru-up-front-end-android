package com.swyp.haruup.di

import com.swyp.haruup.data.local.TokenStorage
import com.swyp.haruup.data.local.TokenStorageImpl
import com.swyp.haruup.data.push.FirebasePushTokenSource
import com.swyp.haruup.data.push.PushTokenSource
import com.swyp.haruup.data.social.KakaoNaverLoginClient
import com.swyp.haruup.data.social.SocialLoginClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SocialModule {

    @Binds
    @Singleton
    abstract fun bindSocialLoginClient(client: KakaoNaverLoginClient): SocialLoginClient

    @Binds
    @Singleton
    abstract fun bindTokenStorage(storage: TokenStorageImpl): TokenStorage

    @Binds
    @Singleton
    abstract fun bindPushTokenSource(source: FirebasePushTokenSource): PushTokenSource
}
