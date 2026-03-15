package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.StatsGraphicsModel
import kotlinx.coroutines.flow.Flow

interface DangerZoneRepository {
    suspend fun getDangerZonesRemote(bounds: MapBounds): List<DangerZoneModel>

    suspend fun getDangerZonesForComparisonRemote(): List<DangerZoneComparisonModel>

    suspend fun saveDangerZoneLocal(zone: DangerZoneModel)

    suspend fun deleteDangerZoneLocal(id: Int)

    suspend fun getGraphicsStatsRemote(zoneId: Int): StatsGraphicsModel

    fun getSavedZoneIds(): Flow<List<Int>>

    fun observeSavedDangerZones(): Flow<List<DangerZoneModel>>

}
