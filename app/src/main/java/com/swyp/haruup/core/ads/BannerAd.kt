package com.swyp.haruup.core.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.swyp.haruup.BuildConfig
import com.swyp.haruup.core.designsystem.HaruUpColor

/** iOS 의 AdSizeBanner 와 같은 320x50 고정 배너입니다. */
private val BANNER_WIDTH = 320.dp
private val BANNER_HEIGHT = 50.dp

/**
 * 홈과 마이페이지 아래에 붙는 배너. iOS 의 BannerView 에 대응합니다.
 *
 * 광고 단위 ID 는 local.properties 에서 들어옵니다. 없으면 Google 테스트 광고가 나옵니다.
 * 미리보기에서는 광고 SDK 를 띄울 수 없어 자리만 칠해 둡니다.
 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val sizeModifier = modifier.size(BANNER_WIDTH, BANNER_HEIGHT)

    if (LocalInspectionMode.current) {
        Box(sizeModifier.background(HaruUpColor.Neutral50))
        return
    }

    AndroidView(
        modifier = sizeModifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = BuildConfig.ADMOB_BANNER_UNIT_ID
                loadAd(AdRequest.Builder().build())
            }
        },
        onRelease = { it.destroy() },
    )
}
