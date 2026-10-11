package com.swyp.haruup.presentation.mypage

import com.swyp.haruup.data.model.WithdrawRequest
import com.swyp.haruup.util.FakeAuthService
import com.swyp.haruup.util.FakeTokenStorage
import com.swyp.haruup.util.MainDispatcherRule
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * 로그아웃·탈퇴가 서버가 요구하는 값을 싣는지, 결과에 따라 토큰을 맞게 다루는지 확인합니다.
 */
class SignOutTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private fun storage() = FakeTokenStorage(accessToken = "access", refreshToken = "refresh")

    @Test
    fun `로그아웃은 refreshToken 을 실어 보내고 토큰을 지운다`() = runTest {
        val service = FakeAuthService()
        val storage = storage()
        val vm = MyPageViewModel(service, storage)

        var signedOut = false
        vm.onLogoutConfirm { signedOut = true }
        advanceUntilIdle()

        assertEquals(listOf("refresh"), service.logoutTokens)
        assertTrue(signedOut)
        assertNull(storage.getAccessToken())
    }

    @Test
    fun `refreshToken 이 없으면 서버에 보내지 않고 나간다`() = runTest {
        val service = FakeAuthService()
        val vm = MyPageViewModel(service, FakeTokenStorage(accessToken = "access"))

        var signedOut = false
        vm.onLogoutConfirm { signedOut = true }
        advanceUntilIdle()

        assertTrue(service.logoutTokens.isEmpty())
        assertTrue(signedOut)
    }

    @Test
    fun `로그아웃이 실패하면 토큰을 남기고 알린다`() = runTest {
        val storage = storage()
        val vm = MyPageViewModel(FakeAuthService(shouldFail = true), storage)

        var signedOut = false
        vm.onLogoutConfirm { signedOut = true }
        advanceUntilIdle()

        assertFalse(signedOut)
        assertEquals("refresh", storage.getRefreshToken())
        assertTrue(vm.uiState.value.dialog is MyPageDialog.Error)
    }

    @Test
    fun `탈퇴는 빈 비밀번호를 담아 보낸다`() = runTest {
        val service = FakeAuthService()
        val vm = MyPageViewModel(service, storage())

        vm.onWithdrawConfirm()
        advanceUntilIdle()

        assertEquals(listOf(WithdrawRequest(password = "")), service.withdrawRequests)
        assertEquals(MyPageDialog.WithdrawSuccess, vm.uiState.value.dialog)
    }
}
