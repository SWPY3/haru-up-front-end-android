package com.swyp.haruup.core.analytics

import android.content.Context
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.ExperimentalAmplitudeFeature
import com.amplitude.android.autocaptureOptions

/**
 * 이벤트를 보내는 곳. iOS 의 AnalyticsManager 에 대응합니다.
 *
 * 화면 곳곳에서 바로 부를 수 있게 iOS 처럼 하나만 둡니다.
 * 키가 없으면 아무것도 보내지 않습니다. 개발 중이나 테스트에서 실제 데이터가 섞이지 않게 하려는 것입니다.
 */
object Analytics {

    /** 실제로 보내는 곳. 테스트에서는 기록용으로 바꿔 끼웁니다. */
    @Volatile
    internal var sink: (eventName: String, properties: Map<String, Any>?) -> Unit = { _, _ -> }

    /** 요소 클릭 자동 수집은 SDK 에서 실험 기능이라 명시적으로 켭니다. iOS 는 .all 로 함께 켜져 있습니다. */
    @OptIn(ExperimentalAmplitudeFeature::class)
    fun init(context: Context, apiKey: String) {
        if (apiKey.isBlank()) return

        val amplitude = Amplitude(
            Configuration(
                apiKey = apiKey,
                context = context.applicationContext,
                // iOS 의 autocapture: .all 과 같게 켭니다.
                autocapture = autocaptureOptions {
                    +sessions
                    +appLifecycles
                    +deepLinks
                    +screenViews
                    +elementInteractions
                },
            )
        )
        sink = { name, properties -> amplitude.track(name, properties) }
    }

    fun track(event: AppEvent, properties: Map<String, Any>? = null) {
        sink(event.eventName, properties)
    }
}
