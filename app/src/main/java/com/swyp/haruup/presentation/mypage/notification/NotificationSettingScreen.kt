package com.swyp.haruup.presentation.mypage.notification

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.swyp.haruup.core.component.HaruUpTopBar
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val H_MARGIN = 20.dp
private val TOP_BAR_TO_CARD = 21.dp
private val CARD_RADIUS = 16.dp
private val CARD_V_PADDING = 18.dp
private val CARD_H_PADDING = 20.dp
private val TITLE_TO_DESCRIPTION = 8.dp
private val TEXT_TO_SWITCH = 20.dp

/**
 * 마이페이지 > 알림 설정. iOS 의 NotificationSettingViewController 에 대응합니다.
 *
 * 스위치는 앱이 들고 있는 값이 아니라 OS 의 알림 권한을 그대로 비춥니다.
 * 그래서 눌러도 스위치가 움직이지 않고 시스템 설정으로 보냅니다. 거기서 바꾸고 돌아오면
 * 화면이 다시 올라올 때 권한을 다시 읽어 반영합니다. (iOS 와 같은 방식)
 *
 * 저장할 상태도 API 도 없어 ViewModel 을 두지 않았습니다.
 */
@Composable
fun NotificationSettingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(context.areNotificationsEnabled()) }

    // 시스템 설정에서 권한을 바꾸고 돌아왔을 때 스위치가 따라오도록 다시 읽습니다.
    LifecycleResumeEffect(context) {
        isEnabled = context.areNotificationsEnabled()
        onPauseOrDispose { }
    }

    NotificationSettingContent(
        isEnabled = isEnabled,
        onBackClick = onBackClick,
        onSwitchClick = { context.openAppNotificationSettings() },
        modifier = modifier,
    )
}

@Composable
private fun NotificationSettingContent(
    isEnabled: Boolean,
    onBackClick: () -> Unit,
    onSwitchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HaruUpColor.AppWhite)
            .statusBarsPadding(),
    ) {
        HaruUpTopBar(title = "알림 설정", onBackClick = onBackClick)

        Spacer(Modifier.height(TOP_BAR_TO_CARD))

        val shape = RoundedCornerShape(CARD_RADIUS)

        Column(
            modifier = Modifier
                .padding(horizontal = H_MARGIN)
                .fillMaxWidth()
                .clip(shape)
                .background(HaruUpColor.Neutral10, shape)
                .padding(horizontal = CARD_H_PADDING, vertical = CARD_V_PADDING),
        ) {
            Text(
                text = "앱 푸시 알림",
                style = HaruUpType.subtitle2,
                color = HaruUpColor.AppBlack,
            )

            Spacer(Modifier.height(TITLE_TO_DESCRIPTION))

            // 스위치는 설명 문구의 첫 줄에 맞춰 놓입니다. (iOS 의 제약과 동일)
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "진행중인 미션의 리마인드 안내를\n푸시 알림으로 받으실 수 있습니다.",
                    style = HaruUpType.body4,
                    color = HaruUpColor.Neutral800,
                    modifier = Modifier.weight(1f),
                )

                Spacer(Modifier.width(TEXT_TO_SWITCH))

                Switch(
                    checked = isEnabled,
                    // 스위치가 직접 값을 바꾸지 않습니다. 권한은 시스템 설정에서만 바뀝니다.
                    onCheckedChange = { onSwitchClick() },
                    colors = SwitchDefaults.colors(checkedTrackColor = HaruUpColor.Cta),
                )
            }
        }
    }
}

/** 알림이 켜져 있는지 묻습니다. 채널 단위가 아니라 앱 전체 기준입니다. */
private fun Context.areNotificationsEnabled(): Boolean =
    NotificationManagerCompat.from(this).areNotificationsEnabled()

/**
 * 이 앱의 알림 설정 화면을 엽니다.
 * 기기에 따라 해당 화면이 없을 수 있어 그때는 앱 정보 화면으로 떨어집니다.
 */
private fun Context.openAppNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val fallback = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(android.net.Uri.fromParts("package", packageName, null))
        startActivity(fallback)
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun NotificationSettingPreview() {
    HaruUpTheme {
        NotificationSettingContent(isEnabled = true, onBackClick = {}, onSwitchClick = {})
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun NotificationSettingOffPreview() {
    HaruUpTheme {
        NotificationSettingContent(isEnabled = false, onBackClick = {}, onSwitchClick = {})
    }
}
