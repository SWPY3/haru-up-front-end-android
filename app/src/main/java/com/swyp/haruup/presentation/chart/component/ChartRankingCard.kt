package com.swyp.haruup.presentation.chart.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swyp.haruup.R
import com.swyp.haruup.core.designsystem.HaruUpColor
import com.swyp.haruup.core.designsystem.HaruUpTheme
import com.swyp.haruup.core.designsystem.HaruUpType
import com.swyp.haruup.data.model.ChartItem
import com.swyp.haruup.presentation.chart.ChartFilter

private val CARD_RADIUS = 20.dp
private val ROW_H_PADDING = 14.dp
private val ROW_TOP = 18.dp
private val ROW_BOTTOM = 16.dp
private val RANK_SIZE = 24.dp
private val RANK_RADIUS = 4.dp
private val RANK_TO_TITLE = 8.dp
private val TITLE_TO_TAGS = 12.dp
private val TAG_SPACING = 4.dp
private val TAG_HEIGHT = 24.dp
private val TAG_RADIUS = 12.dp
private val TAG_H_PADDING = 10.dp
private val TAGS_TO_COUNT = 16.dp
private val FIRE_SIZE = 16.dp
private val FIRE_TO_COUNT = 4.dp

/** 한 줄에 보여줄 태그 개수입니다. (관심사 > 세부 관심사) */
private const val VISIBLE_TAG_COUNT = 2

/**
 * 월간 미션 차트 TOP5 목록.
 * iOS 의 ChartRankingViewController 안 tableView + ChartRankingCell 에 대응합니다.
 */
@Composable
fun ChartRankingCard(
    items: List<ChartItem>,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(CARD_RADIUS)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HaruUpColor.AppWhite, shape),
    ) {
        items.forEachIndexed { index, item ->
            RankingRow(item)

            // 마지막 줄 뒤에는 구분선을 두지 않습니다.
            if (index < items.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ROW_H_PADDING)
                        .height(1.dp)
                        .background(HaruUpColor.Neutral50),
                )
            }
        }
    }
}

@Composable
private fun RankingRow(item: ChartItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = ROW_H_PADDING, end = 20.dp, top = ROW_TOP, bottom = ROW_BOTTOM),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RankBadge(item.rank)

            Spacer(Modifier.width(RANK_TO_TITLE))

            Text(
                text = item.title,
                style = HaruUpType.subtitle2,
                color = HaruUpColor.AppBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(TITLE_TO_TAGS))

        // 태그는 순위 배지만큼 들여써서 제목과 왼쪽을 맞춥니다.
        Row(
            modifier = Modifier.padding(start = RANK_SIZE + RANK_TO_TITLE),
            horizontalArrangement = Arrangement.spacedBy(TAG_SPACING),
        ) {
            item.tags.take(VISIBLE_TAG_COUNT).forEachIndexed { index, tag ->
                // 첫 태그(관심사)에만 이모지를 붙입니다.
                InterestTag(if (index == 0) "${ChartFilter.interestIcon(tag)} $tag" else tag)
            }
        }

        Spacer(Modifier.height(TAGS_TO_COUNT))

        Row(
            modifier = Modifier.padding(start = RANK_SIZE + RANK_TO_TITLE),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_challenge_fire),
                contentDescription = null,
                modifier = Modifier.size(FIRE_SIZE),
            )

            Spacer(Modifier.width(FIRE_TO_COUNT))

            Text(
                text = "${item.count}명이 선택했어요",
                style = HaruUpType.footnote,
                color = HaruUpColor.PrimaryBlue500,
            )
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    val shape = RoundedCornerShape(RANK_RADIUS)

    Box(
        modifier = Modifier
            .size(RANK_SIZE)
            .clip(shape)
            .background(HaruUpColor.PrimaryBlue100, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "$rank", style = HaruUpType.subtitle2, color = HaruUpColor.Cta)
    }
}

@Composable
private fun InterestTag(text: String) {
    val shape = RoundedCornerShape(TAG_RADIUS)

    Box(
        modifier = Modifier
            .height(TAG_HEIGHT)
            .clip(shape)
            .background(HaruUpColor.Neutral10, shape)
            .padding(horizontal = TAG_H_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = HaruUpType.body5,
            color = HaruUpColor.Neutral700,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
private fun ChartRankingCardPreview() {
    HaruUpTheme {
        ChartRankingCard(
            items = listOf(
                ChartItem(1, "스쿼트 실시", listOf("체력관리 및 운동", "헬스"), 4),
                ChartItem(2, "모의 시험 풀기", listOf("자격증 공부", "기술 분야"), 3),
                ChartItem(3, "칼로리 기록하기", listOf("체력관리 및 운동", "자전거"), 3),
            ),
            modifier = Modifier.padding(20.dp),
        )
    }
}
