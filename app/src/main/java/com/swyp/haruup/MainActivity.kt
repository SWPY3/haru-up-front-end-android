package com.swyp.haruup

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.data.push.PushEvents
import com.swyp.haruup.presentation.navigation.HaruUpNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var pushEvents: PushEvents

    /** 결과는 쓰지 않습니다. 허용 여부는 알림 설정 화면이 그때그때 시스템에 묻습니다. */
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // iOS 는 앱을 켤 때 알림 권한을 묻습니다. Android 13 부터는 같은 권한을 직접 물어야 합니다.
        // 이미 답했으면 시스템이 다시 묻지 않습니다.
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 꺼져 있던 앱을 알림으로 연 경우입니다. 회전 등으로 다시 만들어질 때는 이미 처리했으니 넘깁니다.
        if (savedInstanceState == null) handleNotification(intent)

        setContent {
            HaruUpTheme {
                HaruUpNavHost()
            }
        }
    }

    /** 앱이 떠 있을 때 알림을 누른 경우입니다. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotification(intent)
    }

    /** 알림 data 의 type 은 시스템이 띄운 알림이든 직접 띄운 알림이든 extra 로 들어옵니다. */
    private fun handleNotification(intent: Intent?) {
        pushEvents.onNotificationOpened(intent?.getStringExtra(PushEvents.KEY_TYPE))
    }
}
