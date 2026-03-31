package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.TestAuthRepository
import alejandro.developer.data.datasources.DangerZoneDataSource
import alejandro.developer.data.local.AppDatabase
import alejandro.developer.data.local.datasources.DangerZoneLocalDataSource
import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.data.testDangerZone
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DangerZoneRepositoryRoomIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var authRepository: TestAuthRepository
    private lateinit var repository: DangerZoneRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        authRepository = TestAuthRepository(initialUserId = "danger-user-1")
        repository = DangerZoneRepositoryImpl(
            dataSource = object : DangerZoneDataSource {
                override suspend fun getDangerZonesForComparison(): List<DangerZoneComparisonDto> {
                    error("Not used in local integration tests")
                }

                override suspend fun getDangerZones(bounds: alejandro.developer.domain.models.MapBounds): List<DangerZoneDto> {
                    error("Not used in local integration tests")
                }

                override suspend fun getGraphicsStats(zoneId: Int): StatsGraphicsDto {
                    error("Not used in local integration tests")
                }
            },
            local = DangerZoneLocalDataSource(database.dangerZoneDao()),
            authRepository = authRepository
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeSavedDangerZones_switchesDataWhenUserChanges() = runTest {
        repository.saveDangerZoneLocal(
            testDangerZone(id = 12, zoneName = "Lavapies")
        )

        authRepository.setCurrentUserId("danger-user-2")
        repository.saveDangerZoneLocal(
            testDangerZone(id = 44, zoneName = "Vallecas")
        )

        authRepository.setCurrentUserId("danger-user-1")

        repository.observeSavedDangerZones().test {
            val firstUserZones = awaitItem()
            assertEquals(listOf(12), firstUserZones.map { it.id })
            assertEquals(listOf(1, 2), firstUserZones.first().points.map { it.order })

            authRepository.setCurrentUserId("danger-user-2")

            val secondUserZones = awaitItem()
            assertEquals(listOf(44), secondUserZones.map { it.id })
            assertEquals("Vallecas", secondUserZones.first().zoneName)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun savedZoneIds_and_delete_respectCurrentUserScope() = runTest {
        repository.saveDangerZoneLocal(
            testDangerZone(id = 12, zoneName = "Lavapies")
        )

        authRepository.setCurrentUserId("danger-user-2")
        repository.saveDangerZoneLocal(
            testDangerZone(id = 44, zoneName = "Vallecas")
        )

        authRepository.setCurrentUserId("danger-user-1")

        repository.getSavedZoneIds().test {
            assertEquals(listOf(12), awaitItem())

            repository.deleteDangerZoneLocal(12)
            assertEquals(emptyList<Int>(), awaitItem())

            authRepository.setCurrentUserId("danger-user-2")
            assertEquals(listOf(44), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
