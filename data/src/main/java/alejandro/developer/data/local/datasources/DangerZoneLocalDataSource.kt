package alejandro.developer.data.local.datasources

import alejandro.developer.data.local.daos.DangerZoneDao
import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.local.relations.DangerZoneWithPoints
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class DangerZoneLocalDataSource @Inject constructor(
    private val dao: DangerZoneDao
) {

    suspend fun getDangerZones(): List<DangerZoneWithPoints> {
        return dao.getAllDangerZones()
    }

    suspend fun insertZone(zone: DangerZoneEntity) {
        dao.insertDangerZone(zone)
    }

    suspend fun insertPoints(points: List<GeoPointEntity>) {
        dao.insertGeoPoints(points)
    }

    fun getSavedZoneIds(): Flow<List<Int>> {
        return dao.getSavedZoneIds()
    }

}