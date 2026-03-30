package alejandro.developer.data.remote.datasources

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.requireInternet
import alejandro.developer.data.datasources.DangerZoneDataSource
import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.domain.models.MapBounds
import jakarta.inject.Inject

class DangerZoneRemoteDataSource @Inject constructor(
    private val api: DangerZoneApi,
    private val networkMonitor: NetworkMonitor
) : DangerZoneDataSource {

    override suspend fun getDangerZonesForComparison(): List<DangerZoneComparisonDto> {
        networkMonitor.requireInternet()
        return api.getDangerZonesForComparison()
    }

    override suspend fun getDangerZones(bounds: MapBounds): List<DangerZoneDto> {
        networkMonitor.requireInternet()
        return api.getDangerZones(
            bounds.minLat,
            bounds.maxLat,
            bounds.minLng,
            bounds.maxLng
        )
    }

    override suspend fun getGraphicsStats(
        zoneId: Int
    ): StatsGraphicsDto {
        networkMonitor.requireInternet()
        return api.getGraphicsStats(zoneId)
    }
}
