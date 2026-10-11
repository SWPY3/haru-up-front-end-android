package com.swyp.haruup

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp

/**
 * iOS 의 AppDelegate 에 대응합니다.
 *
 * 앱 키는 local.properties 에서 읽어 BuildConfig 로 들어옵니다.
 * 키가 없으면 그 SDK 는 초기화하지 않습니다. 빈 키로 초기화하면 SDK 가 터지고,
 * 그러면 로그인뿐 아니라 앱 전체가 뜨지 않기 때문입니다.
 */
@HiltAndroidApp
class HaruUpApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.KAKAO_NATIVE_APP_KEY.isNotBlank()) {
            KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
        }

        if (BuildConfig.NAVER_CLIENT_ID.isNotBlank() && BuildConfig.NAVER_CLIENT_SECRET.isNotBlank()) {
            NaverIdLoginSDK.initialize(
                this,
                BuildConfig.NAVER_CLIENT_ID,
                BuildConfig.NAVER_CLIENT_SECRET,
                getString(R.string.app_name),
            )
        }

        // TODO: FirebaseApp.initializeApp(this)
    }
}
