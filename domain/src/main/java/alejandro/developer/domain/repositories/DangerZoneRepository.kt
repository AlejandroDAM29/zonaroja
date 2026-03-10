package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.StatsGraphicsModel

interface DangerZoneRepository {
    suspend fun getDangerZonesRemote(bounds: MapBounds): List<DangerZoneModel>

    suspend fun saveDangerZoneLocal(zone: DangerZoneModel)

    suspend fun getGraphicsStatsRemote(zoneId: Int): StatsGraphicsModel
}