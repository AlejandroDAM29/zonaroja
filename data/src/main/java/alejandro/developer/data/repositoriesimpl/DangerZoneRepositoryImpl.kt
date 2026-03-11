package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.local.datasources.DangerZoneLocalDataSource
import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.mappers.toEntity
import alejandro.developer.data.mappers.toGeoEntities
import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.data.remote.datasources.DangerZoneRemoteDataSource
import alejandro.developer.domain.repositories.DangerZoneRepository
import jakarta.inject.Inject
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.StatsGraphicsModel
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class DangerZoneRepositoryImpl @Inject constructor(
    private val remote: DangerZoneRemoteDataSource,
    private val local: DangerZoneLocalDataSource
) : DangerZoneRepository {

    override suspend fun getDangerZonesRemote(bounds: MapBounds): List<DangerZoneModel> {

        return remote.getDangerZones(
            bounds
        ).map { it.toDomain() }
    }

    override suspend fun saveDangerZoneLocal(zone: DangerZoneModel) {

        local.insertZone(zone.toEntity())
        local.insertPoints(zone.toGeoEntities())
    }

    override suspend fun getGraphicsStatsRemote(
        zoneId: Int
    ): StatsGraphicsModel {

        return remote.getGraphicsStats(zoneId).toDomain()
    }

    override fun getSavedZoneIds(): Flow<List<Int>> {
        val test = local.getSavedZoneIds()
        test.map { myIds ->
            Log.i("test-100", "Entra con 2 ids: $myIds");
        }
        return local.getSavedZoneIds()
    }


}