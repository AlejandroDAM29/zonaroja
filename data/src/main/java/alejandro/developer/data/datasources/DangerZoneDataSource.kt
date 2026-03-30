package alejandro.developer.data.datasources

import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.domain.models.MapBounds

interface DangerZoneDataSource {
    suspend fun getDangerZonesForComparison(): List<DangerZoneComparisonDto>

    suspend fun getDangerZones(bounds: MapBounds): List<DangerZoneDto>

    suspend fun getGraphicsStats(zoneId: Int): StatsGraphicsDto
}
