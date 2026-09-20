package com.swyp.haruup

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * iOS 의 AppDelegate 에 대응합니다.
 * 외부 SDK(Kakao / Naver / Firebase / Amplitude) 초기화를 여기에서 수행합니다.
 */
@HiltAndroidApp
class HaruUpApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO: KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        // TODO: NidOAuth.initialize(...)
        // TODO: FirebaseApp.initializeApp(this)
    }
}
