package com.swyp.haruup.network

import com.swyp.haruup.di.NetworkModule
import kotlinx.serialization.builtins.serializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 결과만 알면 되는 API 의 응답을 앱 설정 그대로 읽어 봅니다.
 *
 * 서버가 data 에 문자열을 주는데 Unit 으로 받으면 파싱이 깨지고,
 * 화면은 서버가 성공했는데도 실패로 알립니다.
 */
class ApiResponseDecodingTest {

    private val json = NetworkModule.provideJson()

    @Test
    fun `문자열 data 는 String 으로 읽는다`() {
        val response = json.decodeFromString(
            ApiResponse.serializer(String.serializer()),
            """{"success":true,"data":"관심사가 수정되었습니다."}""",
        )

        assertTrue(response.success)
        assertEquals("관심사가 수정되었습니다.", response.data)
    }

    @Test
    fun `객체 data 는 Unit 으로 받아도 성공 여부를 읽는다`() {
        // 프로필 수정은 data 에 회원 프로필 객체가 옵니다.
        val response = json.decodeFromString(
            ApiResponse.serializer(Unit.serializer()),
            """{"success":true,"data":{"id":1,"nickname":"하루"}}""",
        )

        assertTrue(response.success)
    }
}
