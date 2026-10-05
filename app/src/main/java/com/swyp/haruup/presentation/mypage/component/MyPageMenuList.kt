package com.swyp.haruup.presentation.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.mypage.MyPageMenu

private val LIST_RADIUS = 14.dp
private val ROW_HEIGHT = 60.dp
private val ROW_H_PADDING = 20.dp
private val ARROW_SIZE = 15.dp

/**
 * 마이페이지 메뉴 목록. iOS 의 menuStackView 에 대응합니다.
 *
 * 각 줄 아래에 구분선이 있고 마지막 줄에만 없습니다.
 */
@Composable
fun MyPageMenuList(
    menus: List<MyPageMenu>,
    onMenuClick: (MyPageMenu) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(LIST_RADIUS)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape),
    ) {
        menus.forEachIndexed { index, menu ->
            MenuRow(menu = menu, onClick = { onMenuClick(menu) })

            if (index < menus.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(HaruUpColor.Neutral50),
                )
            }
        }
    }
}

@Composable
private fun MenuRow(menu: MyPageMenu, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ROW_HEIGHT)
            .clickable(onClick = onClick)
            .padding(horizontal = ROW_H_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = menu.title,
            style = HaruUpType.body1,
            color = if (menu.isDestructive) HaruUpColor.Neutral600 else HaruUpColor.AppBlack,
            modifier = Modifier.weight(1f),
        )

        if (menu.hasArrow) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(ARROW_SIZE),
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun MyPageMenuListPreview() {
    HaruUpTheme {
        MyPageMenuList(
            menus = MyPageMenu.entries,
            onMenuClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
