package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.datasources.DangerZoneDataSource
import alejandro.developer.data.sampleStatsGraphicsDto
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GraphicsRepositoryImplTest {

    private val dataSource: DangerZoneDataSource = mock()

    @Test
    fun getGraphicsStats_mapsRemoteDtoToDomain() = runTest {
        whenever(dataSource.getGraphicsStats(12)).thenReturn(sampleStatsGraphicsDto())

        val result = GraphicsRepositoryImpl(dataSource).getGraphicsStats(12)

        assertEquals(900, result.economy.hoodRent)
        assertEquals(2, result.demography.size)
        verify(dataSource).getGraphicsStats(12)
    }
}
