package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.domain.repositories.DangerZoneRepository
import jakarta.inject.Inject
import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.MapBounds
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