package com.swyp.haruup.core.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 광고 동의를 받고 광고 SDK 를 시작합니다. iOS 의 AdConsentManager 에 대응합니다.
 *
 * 순서는 iOS 와 같습니다.
 * 1. 동의 정보를 갱신합니다. (앱을 열 때마다)
 * 2. 동의가 필요한 지역(EEA·영국 등)이면 동의 창을 띄웁니다.
 * 3. 광고를 요청해도 되는 상태일 때만 SDK 를 한 번 시작합니다.
 *
 * iOS 는 3 전에 추적 허용(ATT)을 묻지만 Android 에는 해당하는 권한이 없습니다.
 */
object AdConsent {

    private const val TAG = "AdConsent"

    private val isStarted = AtomicBoolean(false)

    fun requestAndInitialize(activity: Activity) {
        val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    formError?.let { Log.w(TAG, "동의 창을 띄우지 못했습니다: ${it.message}") }
                    startIfAllowed(activity)
                }
            },
            { error ->
                // 갱신에 실패해도 이전에 받은 동의로 광고를 띄울 수 있으면 시작합니다. (iOS 와 같은 흐름)
                Log.w(TAG, "동의 정보를 갱신하지 못했습니다: ${error.message}")
                startIfAllowed(activity)
            },
        )
    }

    private fun startIfAllowed(activity: Activity) {
        if (!UserMessagingPlatform.getConsentInformation(activity).canRequestAds()) return
        if (!isStarted.compareAndSet(false, true)) return

        MobileAds.initialize(activity.applicationContext)
    }
}
