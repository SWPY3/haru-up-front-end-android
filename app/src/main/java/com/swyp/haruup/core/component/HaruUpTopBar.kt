package com.swyp.haruup.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val BAR_HEIGHT = 56.dp
private val BACK_LEADING = 20.dp
private val BACK_SIZE = 20.dp
private val BACK_TO_TITLE = 13.dp

/**
 * 뒤로 가기 버튼과 제목이 왼쪽에 나란히 놓인 상단바.
 * iOS 의 각 화면에 있는 customNavBar + backButton + navTitleLabel 에 대응합니다.
 *
 * 마이페이지 하위 화면(알림 설정 / 프로필 수정 / 관심사 수정)이 모두 같은 모양이라
 * 공통으로 두었습니다.
 */
@Composable
fun HaruUpTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(BAR_HEIGHT)
            .background(HaruUpColor.AppWhite)
            .padding(start = BACK_LEADING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = "뒤로 가기",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(BACK_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBackClick,
                ),
        )

        Spacer(Modifier.width(BACK_TO_TITLE))

        Text(text = title, style = HaruUpType.title3, color = HaruUpColor.AppBlack)
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun HaruUpTopBarPreview() {
    HaruUpTheme {
        HaruUpTopBar(title = "알림 설정", onBackClick = {})
    }
}
