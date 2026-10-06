package com.swyp.haruup.di

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
}
