package com.swyp.haruup.core.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType

private val FIELD_HEIGHT = 55.dp
private val FIELD_RADIUS = 16.dp
private val FIELD_H_PADDING = 16.dp
private val ARROW_SIZE = 20.dp
private val LIST_TOP_GAP = 4.dp
private val LIST_RADIUS = 12.dp
private val ROW_HEIGHT = 55.dp
private val ROW_LEADING = 16.dp
private val LIST_ELEVATION = 8.dp

/** 4개까지 보이고 그 이상은 스크롤합니다. (iOS 와 같은 규칙) */
private const val VISIBLE_ROW_COUNT = 4

/** 비활성 상태의 흐림 정도입니다. */
private const val DISABLED_ALPHA = 0.5f

/** 드롭다운에 올릴 수 있는 항목입니다. */
interface DropdownItem {
    val id: Int
    val displayName: String
}

/**
 * 눌러서 펼치는 선택 상자. iOS 의 DropdownView 와 선택 버튼을 합친 것입니다.
 *
 * 펼친 목록은 바로 아래에 겹쳐 떠야 해서 주변 요소를 밀어내지 않도록
 * 부모가 이 컴포저블을 Box 안에 두고 zIndex 를 올려 주는 것을 전제로 합니다.
 */
@Composable
fun <T : DropdownItem> HaruUpDropdown(
    label: String,
    placeholder: String,
    selectedName: String?,
    items: List<T>,
    selectedId: Int?,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    Column(modifier = modifier) {
        Text(text = label, style = HaruUpType.body4, color = HaruUpColor.Neutral800)

        Spacer(Modifier.height(8.dp))

        // 펼친 목록은 아래 칸 위에 떠야 하므로 Popup 으로 띄웁니다.
        // 같은 Box 안에 두면 Box 가 목록 높이만큼 커져서 아래 내용을 밀어냅니다.
        var fieldWidth by remember { mutableIntStateOf(0) }

        Box(modifier = Modifier.onSizeChanged { fieldWidth = it.width }) {
            DropdownField(
                text = selectedName ?: placeholder,
                isSelected = selectedName != null,
                isExpanded = isExpanded,
                isEnabled = isEnabled,
                onClick = onToggle,
            )

            if (isExpanded && items.isNotEmpty()) {
                val density = LocalDensity.current

                Popup(
                    alignment = Alignment.TopStart,
                    offset = with(density) {
                        IntOffset(x = 0, y = (FIELD_HEIGHT + LIST_TOP_GAP).roundToPx())
                    },
                    onDismissRequest = onToggle,
                    properties = PopupProperties(focusable = true),
                ) {
                    DropdownList(
                        items = items,
                        selectedId = selectedId,
                        onSelect = onSelect,
                        // 떠 있는 목록이라 폭을 직접 맞춰 줍니다.
                        modifier = Modifier.width(with(density) { fieldWidth.toDp() }),
                    )
                }
            }
        }
    }
}

@Composable
private fun DropdownField(
    text: String,
    isSelected: Boolean,
    isExpanded: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(FIELD_RADIUS)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(FIELD_HEIGHT)
            .alpha(if (isEnabled) 1f else DISABLED_ALPHA)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape)
            .border(
                width = 1.dp,
                // 펼쳐져 있는 동안에는 테두리가 파랗게 바뀝니다.
                color = if (isExpanded) HaruUpColor.Cta else HaruUpColor.Neutral200,
                shape = shape,
            )
            .clickable(
                enabled = isEnabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = FIELD_H_PADDING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = HaruUpType.body1,
            color = if (isSelected) HaruUpColor.Cta else HaruUpColor.Neutral800,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Image(
            painter = painterResource(
                if (isExpanded) R.drawable.ic_chevron_top else R.drawable.ic_chevron_bottom
            ),
            contentDescription = null,
            modifier = Modifier.size(ARROW_SIZE),
        )
    }
}

@Composable
private fun <T : DropdownItem> DropdownList(
    items: List<T>,
    selectedId: Int?,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(LIST_RADIUS)

    LazyColumn(
        modifier = modifier
            .heightIn(max = ROW_HEIGHT * VISIBLE_ROW_COUNT)
            .shadow(LIST_ELEVATION, shape, spotColor = LIST_SHADOW, ambientColor = LIST_SHADOW)
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape),
    ) {
        items(items, key = { it.id }) { item ->
            DropdownRow(
                text = item.displayName,
                isSelected = item.id == selectedId,
                onClick = { onSelect(item) },
            )
        }
    }
}

@Composable
private fun DropdownRow(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ROW_HEIGHT)
            .background(if (isSelected) HaruUpColor.PrimaryBlue50 else HaruUpColor.AppWhite)
            .clickable(onClick = onClick)
            .padding(horizontal = ROW_LEADING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = HaruUpType.body1,
            color = if (isSelected) HaruUpColor.PrimaryBlue700 else HaruUpColor.AppBlack,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** iOS 드롭다운 그림자 색입니다. 디자인 시스템에 없는 값이라 여기서만 씁니다. */
private val LIST_SHADOW = Color(0xFFDAE1F0)

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun HaruUpDropdownPreview() {
    data class Item(override val id: Int, override val displayName: String) : DropdownItem

    val items = listOf(
        Item(1, "외국어 공부"),
        Item(2, "자격증 공부"),
        Item(3, "재테크/투자"),
        Item(4, "체력관리 및 운동"),
        Item(5, "직무 관련 역량 개발"),
    )

    HaruUpTheme {
        Column(modifier = Modifier.padding(20.dp)) {
            HaruUpDropdown(
                label = "관심사",
                placeholder = "관심사 선택",
                selectedName = "외국어 공부",
                items = items,
                selectedId = 1,
                isExpanded = true,
                onToggle = {}, onSelect = {},
            )
        }
    }
}
