package com.swyp.haruup.presentation.login.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpType

/**
 * iOS 의 KakaoLoginButton / NaverLoginButton 에 대응합니다.
 * 높이 54, 모서리 16, 아이콘 20, 아이콘-텍스트 간격 8 로 동일하게 맞췄습니다.
 */
@Composable
fun SocialLoginButton(
    text: String,
    @DrawableRes iconRes: Int,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(BUTTON_HEIGHT),
        enabled = enabled,
        shape = RoundedCornerShape(CORNER_RADIUS),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            // 비활성 상태에서도 브랜드 색을 유지한다. Material 기본 회색 처리를 쓰지 않는다.
            disabledContainerColor = backgroundColor,
            disabledContentColor = contentColor,
        ),
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ICON_TEXT_SPACING, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(ICON_SIZE),
            )
            Text(text = text, style = HaruUpType.socialLogin)
        }
    }
}

private val BUTTON_HEIGHT = 54.dp
private val CORNER_RADIUS = 16.dp
private val ICON_SIZE = 20.dp
private val ICON_TEXT_SPACING = 8.dp
