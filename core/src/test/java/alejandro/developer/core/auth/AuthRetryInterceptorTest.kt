package alejandro.developer.core.auth

import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthRetryInterceptorTest {

    private val tokenProvider: FirebaseTokenProvider = mock()
    private val chain: Interceptor.Chain = mock()

    @Test
    fun intercept_returnsOriginalResponse_whenStatusIsNotUnauthorized() = runTest {
        val request = Request.Builder().url("https://example.com").build()
        val response = buildResponse(request, 200)
        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(request)).thenReturn(response)

        val result = AuthRetryInterceptor(tokenProvider).intercept(chain)

        assertSame(response, result)
        verify(tokenProvider, never()).getToken(forceRefresh = true)
    }

    @Test
    fun intercept_retriesOnceWithFreshToken_whenUnauthorized() = runTest {
        val originalRequest = Request.Builder()
            .url("https://example.com")
            .addHeader("Authorization", "Bearer old-token")
            .build()
        val unauthorized = buildResponse(originalRequest, 401)
        val retriedResponse = buildResponse(originalRequest, 200)
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(chain.proceed(any())).thenReturn(unauthorized, retriedResponse)
        whenever(tokenProvider.getToken(forceRefresh = true)).thenReturn("fresh-token")

        val result = AuthRetryInterceptor(tokenProvider).intercept(chain)

        val requestCaptor = argumentCaptor<Request>()
        verify(chain, times(2)).proceed(requestCaptor.capture())
        val retriedRequest = requestCaptor.allValues.last()
        assertEquals("Bearer fresh-token", retriedRequest.header("Authorization"))
        assertEquals("true", retriedRequest.header("X-Retry"))
        assertSame(retriedResponse, result)
    }

    @Test
    fun intercept_doesNotRetry_whenRequestAlreadyRetried() = runTest {
        val request = Request.Builder()
            .url("https://example.com")
            .addHeader("X-Retry", "true")
            .build()
        val response = buildResponse(request, 401)
        whenever(chain.request()).thenReturn(request)
        whenever(chain.proceed(request)).thenReturn(response)

        val result = AuthRetryInterceptor(tokenProvider).intercept(chain)

        assertSame(response, result)
        verify(tokenProvider, never()).getToken(forceRefresh = true)
    }

    private fun buildResponse(request: Request, code: Int): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 200) "OK" else "Unauthorized")
            .build()
    }
}
