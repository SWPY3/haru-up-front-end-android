package com.swyp.haruup.presentation.agree.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor

private val ROW_HEIGHT = 44.dp
private val CHECKBOX_SIZE = 20.dp
private val CHECKBOX_TO_TEXT = 10.dp
private val ARROW_SIZE = 24.dp

/**
 * iOS 의 AgreementCell 에 대응합니다.
 * 체크박스와 텍스트 어느 쪽을 눌러도 체크가 토글되고, 화살표만 원문 보기로 분리돼 있습니다.
 */
@Composable
fun AgreementRow(
    text: String,
    isChecked: Boolean,
    onCheckClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    onArrowClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ROW_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 체크박스와 라벨을 한 덩어리로 묶어 터치 영역을 넓힙니다.
        Row(
            modifier = Modifier
                .weight(1f)
                .height(ROW_HEIGHT)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Checkbox,
                    onClick = onCheckClick,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(
                    if (isChecked) R.drawable.ic_checkbox_selected
                    else R.drawable.ic_checkbox_unselected
                ),
                contentDescription = null,
                modifier = Modifier.size(CHECKBOX_SIZE),
            )

            Spacer(Modifier.width(CHECKBOX_TO_TEXT))

            Text(
                text = text,
                style = textStyle,
                color = HaruUpColor.Neutral1000,
            )
        }

        if (onArrowClick != null) {
            Image(
                painter = painterResource(R.drawable.ic_chevron_right_gray),
                contentDescription = "약관 원문 보기",
                modifier = Modifier
                    .size(ARROW_SIZE)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onArrowClick,
                    ),
            )
        }
    }
}
