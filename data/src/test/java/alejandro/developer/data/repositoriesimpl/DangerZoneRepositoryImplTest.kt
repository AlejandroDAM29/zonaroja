package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.datasources.DangerZoneDataSource
import alejandro.developer.data.local.datasources.DangerZoneLocalDataSource
import alejandro.developer.data.sampleDangerZoneComparisonDto
import alejandro.developer.data.sampleDangerZoneDto
import alejandro.developer.data.sampleDangerZoneModel
import alejandro.developer.data.sampleDangerZoneWithPoints
import alejandro.developer.data.sampleStatsGraphicsDto
import alejandro.developer.data.session.GUEST_USER_SCOPE
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.repositories.AuthRepository
import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class DangerZoneRepositoryImplTest {

    private val dataSource: DangerZoneDataSource = mock()
    private val local: DangerZoneLocalDataSource = mock()
    private val authRepository: AuthRepository = mock()

    private val repository = DangerZoneRepositoryImpl(
        dataSource = dataSource,
        local = local,
        authRepository = authRepository
    )

    @Test
    fun getDangerZonesForComparisonRemote_mapsDtos() = runTest {
        whenever(dataSource.getDangerZonesForComparison()).thenReturn(listOf(sampleDangerZoneComparisonDto()))

        val result = repository.getDangerZonesForComparisonRemote()

        assertEquals(1, result.size)
        assertEquals("Vallecas", result.first().zoneName)
    }

    @Test
    fun getDangerZonesRemote_mapsDtos() = runTest {
        val bounds = MapBounds(40.0, 41.0, -4.0, -3.0)
        whenever(dataSource.getDangerZones(bounds)).thenReturn(listOf(sampleDangerZoneDto()))

        val result = repository.getDangerZonesRemote(bounds)

        assertEquals(1, result.size)
        assertEquals("Lavapies", result.first().zoneName)
    }

    @Test
    fun saveDangerZoneLocal_usesCurrentUserScopeAndPersistsPoints() = runTest {
        val zone = sampleDangerZoneModel()
        whenever(authRepository.getCurrentUserId()).thenReturn("user-1")
        whenever(local.insertZone(org.mockito.kotlin.any())).thenReturn(88L)

        repository.saveDangerZoneLocal(zone)

        val zoneCaptor = argumentCaptor<alejandro.developer.data.local.entities.DangerZoneEntity>()
        val pointsCaptor = argumentCaptor<List<alejandro.developer.data.local.entities.GeoPointEntity>>()
        verify(local).insertZone(zoneCaptor.capture())
        verify(local).insertPoints(pointsCaptor.capture())
        assertEquals("user-1", zoneCaptor.firstValue.userId)
        assertEquals(88L, pointsCaptor.firstValue.first().dangerZoneLocalId)
        assertEquals(zone.points.size, pointsCaptor.firstValue.size)
    }

    @Test
    fun deleteDangerZoneLocal_usesGuestScopeWhenNoUserExists() = runTest {
        whenever(authRepository.getCurrentUserId()).thenReturn(null)

        repository.deleteDangerZoneLocal(25)

        verify(local).deleteZone(25, GUEST_USER_SCOPE)
    }

    @Test
    fun getGraphicsStatsRemote_mapsDto() = runTest {
        whenever(dataSource.getGraphicsStats(5)).thenReturn(sampleStatsGraphicsDto())

        val result = repository.getGraphicsStatsRemote(5)

        assertEquals(4100, result.economy.cityPrice)
    }

    @Test
    fun getSavedZoneIds_observesCurrentUserScope() = runTest {
        whenever(authRepository.observeCurrentUserId()).thenReturn(MutableStateFlow("user-2"))
        whenever(local.getSavedZoneIds("user-2")).thenReturn(flowOf(listOf(3, 4)))

        repository.getSavedZoneIds().test {
            assertEquals(listOf(3, 4), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeSavedDangerZones_mapsLocalRelationsToDomain() = runTest {
        whenever(authRepository.observeCurrentUserId()).thenReturn(MutableStateFlow("user-2"))
        whenever(local.observeDangerZones("user-2")).thenReturn(flowOf(listOf(sampleDangerZoneWithPoints())))

        repository.observeSavedDangerZones().test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals(listOf(1, 2), item.first().points.map { it.order })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
