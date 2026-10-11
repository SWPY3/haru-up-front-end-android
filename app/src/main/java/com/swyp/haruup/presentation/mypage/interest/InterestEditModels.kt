package com.swyp.haruup.presentation.mypage.interest

import com.swyp.haruup.core.component.DropdownItem
import com.swyp.haruup.data.model.InterestData

/** 드롭다운에 올리기 위해 관심사 항목을 감쌉니다. */
data class InterestOption(
    override val id: Int,
    override val displayName: String,
) : DropdownItem {

    constructor(data: InterestData) : this(data.id, data.name)
}

/** 지금 펼쳐져 있는 드롭다운. 한 번에 하나만 열립니다. */
enum class InterestDropdown { INTEREST, DETAIL, GOAL }

/**
 * 직접 입력을 받는 바텀시트가 어떤 칸을 채우는지 구분합니다.
 *
 * 세부 관심사에서 "기타" 를, 목표에서 "직접 입력" 을 고르면 각각 열립니다.
 */
enum class DirectInputTarget(val title: String) {
    DETAIL(title = "원하는 세부 관심사를 입력해주세요."),
    GOAL(title = "원하는 목표를 입력해주세요."),
}

/**
 * 목록에서 직접 입력 항목을 가려냅니다.
 * 서버가 "기타", "직접", "직접 입력할게요" 등으로 내려줘서 이름에 포함되는지로 봅니다. (iOS 와 동일)
 */
fun InterestOption.isDirectInput(): Boolean =
    displayName.contains("기타") || displayName.contains("직접")
