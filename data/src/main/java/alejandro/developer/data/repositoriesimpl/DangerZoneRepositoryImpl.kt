package alejandro.developer.data.repositoriesimpl

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.requireInternet
import alejandro.developer.data.local.datasources.DangerZoneLocalDataSource
import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.mappers.toEntity
import alejandro.developer.data.mappers.toGeoEntities
import alejandro.developer.data.remote.datasources.DangerZoneRemoteDataSource
import alejandro.developer.data.session.toUserScopeKey
import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.StatsGraphicsModel
import alejandro.developer.domain.repositories.AuthRepository
import alejandro.developer.domain.repositories.DangerZoneRepository
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.collections.map

@OptIn(ExperimentalCoroutinesApi::class)
class DangerZoneRepositoryImpl @Inject constructor(
    private val remote: DangerZoneRemoteDataSource,
    private val local: DangerZoneLocalDataSource,
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor
) : DangerZoneRepository {

    override suspend fun getDangerZonesForComparisonRemote(): List<DangerZoneComparisonModel> {
        networkMonitor.requireInternet()
        return remote.getDangerZonesForComparison().map { it.toDomain() }
    }

    override suspend fun getDangerZonesRemote(bounds: MapBounds): List<DangerZoneModel> {
        networkMonitor.requireInternet()
        return remote.getDangerZones(
            bounds
        ).map { it.toDomain() }
    }

    override suspend fun saveDangerZoneLocal(zone: DangerZoneModel) {
        val userScope = authRepository.getCurrentUserId().toUserScopeKey()
        val localId = local.insertZone(zone.toEntity(userScope))
        local.insertPoints(zone.toGeoEntities(localId))
    }

    override suspend fun deleteDangerZoneLocal(id: Int) {
        local.deleteZone(id, authRepository.getCurrentUserId().toUserScopeKey())
    }

    override suspend fun getGraphicsStatsRemote(
        zoneId: Int
    ): StatsGraphicsModel {
        networkMonitor.requireInternet()
        return remote.getGraphicsStats(zoneId).toDomain()
    }

    override fun getSavedZoneIds(): Flow<List<Int>> {
        return authRepository.observeCurrentUserId().flatMapLatest { currentUserId ->
            local.getSavedZoneIds(currentUserId.toUserScopeKey())
        }
    }

    override fun observeSavedDangerZones(): Flow<List<DangerZoneModel>> {
        return authRepository.observeCurrentUserId().flatMapLatest { currentUserId ->
            local.observeDangerZones(currentUserId.toUserScopeKey()).map { zones ->
                zones.map { it.toDomain() }
            }
        }
    }


}
