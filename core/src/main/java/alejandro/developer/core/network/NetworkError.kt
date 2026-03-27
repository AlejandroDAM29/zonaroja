package alejandro.developer.core.network

import com.google.firebase.FirebaseNetworkException
import java.io.IOException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class NoInternetException : IOException("No internet connection")

fun NetworkMonitor.requireInternet() {
    if (!isCurrentlyOnline()) {
        throw NoInternetException()
    }
}

fun Throwable.isNetworkConnectivityError(): Boolean {
    val rootCause = generateSequence(this) { current ->
        current.cause?.takeIf { it !== current }
    }.last()

    return when (rootCause) {
        is NoInternetException,
        is FirebaseNetworkException,
        is UnknownHostException,
        is ConnectException,
        is NoRouteToHostException,
        is SocketTimeoutException -> true

        is IOException -> {
            rootCause.message?.contains("Unable to resolve host", ignoreCase = true) == true
        }

        else -> false
    }
}
