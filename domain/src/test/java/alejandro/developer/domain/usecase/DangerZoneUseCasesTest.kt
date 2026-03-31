package alejandro.developer.domain.usecase

import alejandro.developer.domain.sampleDangerZoneComparisonModel
import alejandro.developer.domain.sampleDangerZoneModel
import alejandro.developer.domain.sampleMapBounds
import alejandro.developer.domain.repositories.DangerZoneRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DangerZoneUseCasesTest {

    private val repository: DangerZoneRepository = mock()

    @Test
    fun getDangerZones_returnsRepositoryResult() = runTest {
        val bounds = sampleMapBounds()
        val expected = listOf(sampleDangerZoneModel())
        whenever(repository.getDangerZonesRemote(bounds)).thenReturn(expected)

        val result = GetDangerZonesUseCase(repository).invoke(bounds)

        assertEquals(expected, result)
        verify(repository).getDangerZonesRemote(bounds)
    }

    @Test
    fun getDangerZonesComparison_returnsRepositoryResult() = runTest {
        val expected = listOf(sampleDangerZoneComparisonModel())
        whenever(repository.getDangerZonesForComparisonRemote()).thenReturn(expected)

        val result = GetDangerZonesComparisonUseCase(repository).invoke()

        assertEquals(expected, result)
        verify(repository).getDangerZonesForComparisonRemote()
    }

    @Test
    fun saveDangerZone_delegatesToRepository() = runTest {
        val zone = sampleDangerZoneModel()

        SaveDangerZoneUseCase(repository).invoke(zone)

        verify(repository).saveDangerZoneLocal(zone)
    }

    @Test
    fun deleteDangerZone_delegatesToRepository() = runTest {
        DeleteDangerZoneUseCase(repository).invoke(25)

        verify(repository).deleteDangerZoneLocal(25)
    }

    @Test
    fun getSavedZoneIds_returnsRepositoryFlow() {
        val expected = flowOf(listOf(1, 2, 3))
        whenever(repository.getSavedZoneIds()).thenReturn(expected)

        val result = GetSavedZonesUseCase(repository).invoke()

        assertSame(expected, result)
        verify(repository).getSavedZoneIds()
    }

    @Test
    fun getSavedDangerZones_returnsRepositoryFlow() {
        val expected = flowOf(listOf(sampleDangerZoneModel()))
        whenever(repository.observeSavedDangerZones()).thenReturn(expected)

        val result = GetSavedDangerZonesUseCase(repository).invoke()

        assertSame(expected, result)
        verify(repository).observeSavedDangerZones()
    }
}
