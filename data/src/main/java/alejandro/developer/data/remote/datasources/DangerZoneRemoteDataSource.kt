package alejandro.developer.data.remote.datasources

import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.domain.models.MapBounds
import jakarta.inject.Inject

class DangerZoneRemoteDataSource @Inject constructor(
    private val api: DangerZoneApi
) {

    suspend fun getDangerZonesForComparison(): List<DangerZoneComparisonDto> {
        return api.getDangerZonesForComparison()
    }

    suspend fun getDangerZones(bounds: MapBounds): List<DangerZoneDto> {

        return api.getDangerZones(
            bounds.minLat,
            bounds.maxLat,
            bounds.minLng,
            bounds.maxLng
        )
    }

    suspend fun getGraphicsStats(
        zoneId: Int
    ): StatsGraphicsDto {

        return api.getGraphicsStats(zoneId)
    }
}
