package com.swyp.haruup.presentation.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val PROFILE_SIZE = 80.dp
private val PROFILE_TO_NAME = 16.dp
private val NAME_TO_EDIT = 8.dp
private val EDIT_BUTTON_SIZE = 36.dp

/**
 * 캐릭터 이미지 · 닉네임 · 프로필 수정 버튼.
 * iOS MyPageViewController 의 profileImageView / nicknameLabel / editProfileButton 에 대응합니다.
 */
@Composable
fun MyPageProfileHeader(
    displayName: String,
    characterId: Int,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(
                // 1 = 하루, 그 외 = 나루 (iOS 와 같은 판정)
                if (characterId == 1) R.drawable.profile_character_haru
                else R.drawable.profile_character_naru
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(PROFILE_SIZE)
                .clip(CircleShape),
        )

        Spacer(Modifier.width(PROFILE_TO_NAME))

        Text(
            text = displayName,
            style = HaruUpType.subtitle1,
            color = HaruUpColor.AppBlack,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.width(NAME_TO_EDIT))

        Image(
            painter = painterResource(R.drawable.ic_profile_edit),
            contentDescription = "프로필 수정",
            modifier = Modifier
                .size(EDIT_BUTTON_SIZE)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onEditClick,
                ),
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun MyPageProfileHeaderPreview() {
    HaruUpTheme {
        MyPageProfileHeader(
            displayName = "하루나루님",
            characterId = 1,
            onEditClick = {},
            modifier = Modifier.padding(20.dp),
        )
    }
}
