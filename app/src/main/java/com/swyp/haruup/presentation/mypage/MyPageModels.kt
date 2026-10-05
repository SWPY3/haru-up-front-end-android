package com.swyp.haruup.presentation.mypage

/**
 * 마이페이지 메뉴 목록. iOS 의 menuStackView 에 담기는 MyPageMenuButton 들과 같은 순서입니다.
 *
 * iOS 에는 "관심사 수정" 이 맨 위에 하나 더 있지만 `isHidden = true` 로 가려 둔 상태라
 * 실제 앱에서는 보이지 않습니다. 그래서 여기에도 넣지 않았습니다.
 */
enum class MyPageMenu(
    val title: String,
    /** 오른쪽 화살표 표시 여부. 로그아웃·탈퇴에는 없습니다. */
    val hasArrow: Boolean = true,
    /** 탈퇴하기만 흐린 색으로 둡니다. */
    val isDestructive: Boolean = false,
    /** 외부 브라우저로 여는 메뉴면 주소를, 아니면 null */
    val url: String? = null,
) {
    FEEDBACK(
        title = "의견남기기",
        url = "https://docs.google.com/forms/d/e/1FAIpQLScWwqt7_pedkfinoJ5Xtd2TG-IU5xdluYFRm7i1wvqsEaJgKQ/viewform",
    ),
    INQUIRY(
        title = "문의하기",
        url = "https://docs.google.com/forms/d/e/1FAIpQLSckyb5mJ1f6EMVjoY3WhTaJnJ_6ZAFBkqwYKQxgc2JyHDhuxQ/viewform",
    ),
    NOTIFICATION_SETTING(title = "알림 설정"),
    TERMS(
        title = "서비스 이용약관",
        url = "https://melodic-roar-3e1.notion.site/2e0849f596f380eabc6de523ab0d9bd9",
    ),
    PRIVACY_POLICY(
        title = "개인정보 처리방침",
        url = "https://delightful-reply-8f4.notion.site/3c5829a940ae80528102f0ab06b285e8",
    ),
    LOGOUT(title = "로그아웃", hasArrow = false),
    WITHDRAW(title = "탈퇴하기", hasArrow = false, isDestructive = true),
}

/** 마이페이지에서 띄우는 알림창입니다. 한 번에 하나만 뜹니다. */
sealed interface MyPageDialog {

    /** 로그아웃을 진행할지 묻습니다. */
    data object Logout : MyPageDialog

    /** 탈퇴를 진행할지 묻습니다. */
    data object Withdraw : MyPageDialog

    /** 탈퇴가 끝났음을 알립니다. 확인을 누르면 로그인 화면으로 돌아갑니다. */
    data object WithdrawSuccess : MyPageDialog

    data class Error(val message: String) : MyPageDialog
}
