package com.swyp.haruup.presentation.maintab.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.presentation.maintab.MainTab

private val BAR_HEIGHT = 60.dp
private val BAR_RADIUS = 24.dp
private val BAR_ELEVATION = 11.dp
private val CONTENT_TOP = 8.dp
private val ICON_SIZE = 24.dp
private val ICON_TO_LABEL = 6.dp

/**
 * 하단 탭바. iOS 의 MainTabBarView 에 대응합니다.
 *
 * 선택 여부에 따라 아이콘 이미지 자체가 바뀝니다. (iOS 와 같은 방식)
 */
@Composable
fun MainTabBar(
    selectedTab: MainTab?,
    onTabClick: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(topStart = BAR_RADIUS, topEnd = BAR_RADIUS)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = BAR_ELEVATION,
                shape = shape,
                spotColor = HaruUpColor.Shadow.TabBar,
                ambientColor = HaruUpColor.Shadow.TabBar,
            )
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .border(0.5.dp, HaruUpColor.Neutral50, shape)
            .navigationBarsPadding()
            .height(BAR_HEIGHT)
            .padding(top = CONTENT_TOP),
    ) {
        MainTab.entries.forEach { tab ->
            TabItem(
                tab = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabClick(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TabItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(if (isSelected) tab.selectedIcon else tab.unselectedIcon),
            contentDescription = tab.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(ICON_SIZE),
        )

        Spacer(Modifier.height(ICON_TO_LABEL))

        Text(
            text = tab.title,
            style = HaruUpType.caption3,
            color = if (isSelected) HaruUpColor.PrimaryBlue700 else HaruUpColor.Neutral300,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun MainTabBarPreview() {
    HaruUpTheme {
        MainTabBar(selectedTab = MainTab.HOME, onTabClick = {})
    }
}
