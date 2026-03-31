package alejandro.developer.core.network

import java.io.IOException
import java.net.ConnectException
import java.net.UnknownHostException
import kotlinx.coroutines.flow.emptyFlow
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class NetworkErrorTest {

    @Test(expected = NoInternetException::class)
    fun requireInternet_throwsWhenDeviceIsOffline() {
        val monitor = mock<NetworkMonitor>()
        whenever(monitor.isCurrentlyOnline()).thenReturn(false)
        whenever(monitor.isOnline).thenReturn(emptyFlow())

        monitor.requireInternet()
    }

    @Test
    fun requireInternet_doesNothingWhenDeviceIsOnline() {
        val monitor = mock<NetworkMonitor>()
        whenever(monitor.isCurrentlyOnline()).thenReturn(true)
        whenever(monitor.isOnline).thenReturn(emptyFlow())

        monitor.requireInternet()
    }

    @Test
    fun isNetworkConnectivityError_returnsTrueForKnownConnectivityFailures() {
        assertTrue(NoInternetException().isNetworkConnectivityError())
        assertTrue(ConnectException("refused").isNetworkConnectivityError())
        assertTrue(IOException("wrapper", UnknownHostException("host")).isNetworkConnectivityError())
        assertTrue(IOException("Unable to resolve host api.example.com").isNetworkConnectivityError())
    }

    @Test
    fun isNetworkConnectivityError_returnsFalseForUnknownFailures() {
        assertFalse(IllegalStateException("boom").isNetworkConnectivityError())
    }
}
