package com.swyp.haruup.data.push

/**
 * 푸시 토큰과 기기 식별자를 꺼내 옵니다.
 * Firebase 에 직접 기대지 않게 감싸 두어, 설정 파일이 없을 때도 앱이 돌고 테스트도 할 수 있습니다.
 */
interface PushTokenSource {

    /** 지금 기기의 푸시 토큰. Firebase 가 설정되지 않았거나 받지 못하면 null 입니다. */
    suspend fun currentToken(): String?

    /** 기기를 구분하는 값. iOS 의 identifierForVendor 에 대응합니다. */
    fun deviceId(): String
}
