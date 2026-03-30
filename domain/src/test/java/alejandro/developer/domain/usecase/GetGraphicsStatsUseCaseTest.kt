package alejandro.developer.domain.usecase

import alejandro.developer.domain.sampleStatsGraphicsModel
import alejandro.developer.domain.repositories.GraphicsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetGraphicsStatsUseCaseTest {

    private val repository: GraphicsRepository = mock()

    @Test
    fun getGraphicsStats_returnsRepositoryResult() = runTest {
        val expected = sampleStatsGraphicsModel()
        whenever(repository.getGraphicsStats(14)).thenReturn(expected)

        val result = GetGraphicsStatsUseCase(repository).invoke(14)

        assertEquals(expected, result)
        verify(repository).getGraphicsStats(14)
    }
}
