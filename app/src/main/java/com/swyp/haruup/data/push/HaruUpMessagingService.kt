package com.swyp.haruup.data.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.swyp.haruup.MainActivity
import com.swyp.haruup.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FCM 수신. iOS AppDelegate 의 MessagingDelegate / UNUserNotificationCenterDelegate 에 대응합니다.
 *
 * 앱이 뒤에 있을 때 오는 알림은 시스템이 알아서 띄웁니다. 여기서는 앱이 앞에 있을 때만 직접 띄웁니다.
 * iOS 도 앱이 앞에 있을 때 알림 설정이 켜져 있으면 배너를 띄웁니다.
 * Android 는 알림이 꺼져 있으면 시스템이 막아 주므로 따로 확인하지 않습니다.
 */
@AndroidEntryPoint
class HaruUpMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var registrar: PushTokenRegistrar

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        scope.launch { registrar.onNewToken(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val notification = message.notification ?: return
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) return

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            message.data[PushEvents.KEY_TYPE]?.let { putExtra(PushEvents.KEY_TYPE, it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            message.messageId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

        val built = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        @Suppress("MissingPermission") // 위에서 알림 허용 여부를 확인했습니다.
        NotificationManagerCompat.from(this).notify(message.messageId.hashCode(), built)
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    companion object {
        /** 서버가 채널을 지정하지 않아 기본 채널 하나만 씁니다. */
        const val CHANNEL_ID = "haruup_default"

        /** 앱 시작 때 한 번 만듭니다. 이미 있으면 시스템이 무시합니다. */
        fun createChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH,
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
