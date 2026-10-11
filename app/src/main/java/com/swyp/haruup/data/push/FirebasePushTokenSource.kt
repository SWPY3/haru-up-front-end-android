package com.swyp.haruup.data.push

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebasePushTokenSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : PushTokenSource {

    /**
     * google-services.json 이 없으면 Firebase 가 초기화되지 않습니다.
     * 그 상태에서 FirebaseMessaging 을 부르면 앱이 종료되므로 먼저 확인합니다.
     */
    override suspend fun currentToken(): String? {
        if (FirebaseApp.getApps(context).isEmpty()) return null

        return suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                continuation.resume(if (task.isSuccessful) task.result else null)
            }
        }
    }

    /** 앱을 지웠다 깔면 바뀌는 값입니다. iOS identifierForVendor 와 성격이 같습니다. */
    @SuppressLint("HardwareIds")
    override fun deviceId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: UUID.randomUUID().toString()
}
