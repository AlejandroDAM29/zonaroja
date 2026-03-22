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

    suspend fun getDangerZones(userId: String): List<DangerZoneWithPoints> {
        return dao.getAllDangerZones(userId)
    }

    fun observeDangerZones(userId: String): Flow<List<DangerZoneWithPoints>> {
        return dao.observeAllDangerZones(userId)
    }

    suspend fun insertZone(zone: DangerZoneEntity): Long {
        return dao.insertDangerZone(zone)
    }

    suspend fun insertPoints(points: List<GeoPointEntity>) {
        dao.insertGeoPoints(points)
    }

    fun getSavedZoneIds(userId: String): Flow<List<Int>> {
        return dao.getSavedZoneIds(userId)
    }

    suspend fun deleteZone(zoneId: Int, userId: String) {
        dao.deleteDangerZone(zoneId, userId)
    }

}
