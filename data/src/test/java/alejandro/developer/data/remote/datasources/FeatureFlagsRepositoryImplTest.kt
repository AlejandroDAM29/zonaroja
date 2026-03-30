package alejandro.developer.data.remote.datasources

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.data.remote.general.RemoteConfigKeys
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FeatureFlagsRepositoryImplTest {

    private val remoteDataSource: RemoteConfigFirebaseFlagsDataSource = mock()
    private val networkMonitor: NetworkMonitor = mock()

    @Test
    fun getFeatureFlags_fetchesRemoteValuesWhenOnline() = runTest {
        whenever(networkMonitor.isCurrentlyOnline()).thenReturn(true)
        whenever(remoteDataSource.getBoolean(RemoteConfigKeys.GOOGLE_LOGIN_ENABLED)).thenReturn(true)

        val result = FeatureFlagsRepositoryImpl(remoteDataSource, networkMonitor).getFeatureFlags()

        assertTrue(result.googleLoginEnabled)
        verify(remoteDataSource).fetchAndActivate()
        verify(remoteDataSource).getBoolean(RemoteConfigKeys.GOOGLE_LOGIN_ENABLED)
    }

    @Test
    fun getFeatureFlags_skipsFetchWhenOffline() = runTest {
        whenever(networkMonitor.isCurrentlyOnline()).thenReturn(false)
        whenever(remoteDataSource.getBoolean(RemoteConfigKeys.GOOGLE_LOGIN_ENABLED)).thenReturn(false)

        val result = FeatureFlagsRepositoryImpl(remoteDataSource, networkMonitor).getFeatureFlags()

        assertFalse(result.googleLoginEnabled)
        verify(remoteDataSource, never()).fetchAndActivate()
    }
}
