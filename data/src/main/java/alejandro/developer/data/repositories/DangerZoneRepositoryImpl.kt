package alejandro.developer.data.repositories

import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.domain.main.DangerZoneRepository
import jakarta.inject.Inject
import alejandro.developer.domain.main.DangerZone
import alejandro.developer.domain.main.MapBounds
import kotlin.collections.map

class DangerZoneRepositoryImpl @Inject constructor(
    private val api: DangerZoneApi
) : DangerZoneRepository {

    override suspend fun getDangerZones(bounds: MapBounds): List<DangerZone> {
        return api.getDangerZones(
            bounds.minLat,
            bounds.maxLat,
            bounds.minLng,
            bounds.maxLng
        ).map { it.toDomain() }
    }
}