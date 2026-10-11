package com.swyp.haruup.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.swyp.haruup.data.model.JobDetail
import com.swyp.haruup.di.NetworkModule
import com.swyp.haruup.network.service.JobService
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

/**
 * 직업 API 는 ApiResponse 로 감싸지 않은 배열을 줍니다.
 * 다른 API 처럼 감싸진 것으로 받으면 파싱이 깨지므로 실제 응답 모양으로 확인합니다.
 */
class JobServiceDecodingTest {

    private lateinit var server: MockWebServer
    private lateinit var service: JobService

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        service = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(NetworkModule.provideJson().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(JobService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `세부 직무 배열을 그대로 읽고 jobId 를 쿼리로 보낸다`() = runTest {
        // 서버 JobDetailDto 는 BaseEntity 를 상속해 다른 필드도 함께 옵니다.
        server.enqueue(
            MockResponse().setBody(
                """[{"id":2,"jobId":1,"jobDetailName":"IT/개발자","createdAt":"2026-01-01T00:00:00","deleted":false}]"""
            )
        )

        val details = service.jobDetails(1)

        assertEquals("/api/job/getJobDetailList?jobId=1", server.takeRequest().path)
        assertEquals(listOf(JobDetail(id = 2, jobId = 1, jobDetailName = "IT/개발자")), details)
    }
}
