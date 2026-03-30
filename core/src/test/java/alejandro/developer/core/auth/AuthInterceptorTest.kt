package alejandro.developer.core.auth

import kotlinx.coroutines.test.runTest
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthInterceptorTest {

    private val tokenProvider: FirebaseTokenProvider = mock()
    private val chain: Interceptor.Chain = mock()

    @Test
    fun intercept_addsAuthorizationHeaderWithCurrentToken() = runTest {
        val originalRequest = Request.Builder()
            .url("https://example.com/zones")
            .build()
        val response = Response.Builder()
            .request(originalRequest)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()
        whenever(tokenProvider.getToken()).thenReturn("token-123")
        whenever(chain.request()).thenReturn(originalRequest)
        whenever(chain.proceed(any())).thenReturn(response)

        val result = AuthInterceptor(tokenProvider).intercept(chain)

        val requestCaptor = argumentCaptor<Request>()
        verify(chain).proceed(requestCaptor.capture())
        assertEquals("Bearer token-123", requestCaptor.firstValue.header("Authorization"))
        assertSame(response, result)
    }
}
