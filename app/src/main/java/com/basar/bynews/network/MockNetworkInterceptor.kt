package com.basar.bynews.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class MockNetworkInterceptor : Interceptor {

    private val mockResponses = mutableListOf<MockResponse>()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val mockResponse = findMockResponseInList(request) ?: throw RuntimeException(
            "No mock response found for url ${request.url}. Please define a mock response in your MockApi!"
        )

        simulateNetworkDelay(mockResponse)
        return if (mockResponse.status >= 400) {
            createErrorResponse(request, mockResponse.body)
        } else {
            createSuccessResponse(mockResponse, request)
        }
    }

    private fun findMockResponseInList(request: Request): MockResponse? {
        return mockResponses.find { mockResponse ->
            mockResponse.path.contains(request.url.encodedPath)
        }
    }

    private fun simulateNetworkDelay(mockResponse: MockResponse) {
        Thread.sleep(mockResponse.delayInMs)
    }

    private fun createErrorResponse(request: Request, errorBody: String = "Error"): Response {
        return Response.Builder()
            .code(500)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("Internal Server Error: $errorBody")
            .body(
                errorBody.toResponseBody("text/plain".toMediaType())
            )
            .build()
    }

    private fun createSuccessResponse(
        mockResponse: MockResponse,
        request: Request
    ): Response {
        return Response.Builder()
            .code(mockResponse.status)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(
                mockResponse.body.toResponseBody("application/json".toMediaType())
            )
            .build()
    }

    fun mock(
        path: String,
        body: String,
        status: Int,
        delayInMs: Long = 250,
    ) = apply {
        val mockResponse = MockResponse(
            path = path,
            body = body,
            status = status,
            delayInMs = delayInMs,
        )
        mockResponses.add(mockResponse)
    }
}

data class MockResponse(
    val path: String,
    val body: String,
    val status: Int,
    val delayInMs: Long,
)
