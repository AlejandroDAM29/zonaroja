package alejandro.developer.data.providers

import alejandro.developer.data.sampleFeatureFlagsModel
import alejandro.developer.domain.repositories.FeatureFlagsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FeatureFlagsProviderTest {

    private val repository: FeatureFlagsRepository = mock()

    @Test
    fun get_cachesFlagsAfterFirstLoad() = runTest {
        whenever(repository.getFeatureFlags()).thenReturn(sampleFeatureFlagsModel(enabled = true))
        val provider = FeatureFlagsProvider(repository)

        val first = provider.get()
        val second = provider.get()

        assertEquals(first, second)
        verify(repository, times(1)).getFeatureFlags()
    }
}
