package com.swyp.haruup.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/** 브라우저가 없어 링크를 열지 못했을 때 보여 줄 문구 */
const val LINK_OPEN_FAILED_MESSAGE = "링크를 열 수 있는 브라우저가 없어요."

/**
 * 약관 같은 외부 링크를 앱 안 브라우저 탭으로 엽니다.
 *
 * 브라우저가 없는 기기에서는 예외가 나서 앱이 꺼집니다.
 * 그래서 잡아서 false 를 돌려주고, 알리는 일은 화면에 맡깁니다.
 */
fun Context.openExternalLink(url: String): Boolean =
    try {
        CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
        true
    } catch (e: ActivityNotFoundException) {
        false
    }
