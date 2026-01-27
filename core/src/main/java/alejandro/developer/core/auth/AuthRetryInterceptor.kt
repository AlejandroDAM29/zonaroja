package alejandro.developer.core.auth

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthRetryInterceptor @Inject constructor(
    private val tokenProvider: FirebaseTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val response = chain.proceed(request)

        if (response.code != 401) {
            return response
        }

        if (request.header("X-Retry") == "true") {
            return response
        }

        response.close()

        val newToken = runBlocking {
            tokenProvider.getToken(forceRefresh = true)
        }

        val newRequest = request.newBuilder()
            .removeHeader("Authorization")
            .addHeader("Authorization", "Bearer $newToken")
            .addHeader("X-Retry", "true") // marca de retry
            .build()

        return chain.proceed(newRequest)
    }
}