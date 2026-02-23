package alejandro.developer.data.repositories

import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.remote.apis.DangerZoneApi
import alejandro.developer.domain.main.DangerZoneRepository
import jakarta.inject.Inject
import alejandro.developer.domain.main.DangerZone
import kotlin.collections.map

class DangerZoneRepositoryImpl @Inject constructor(
    private val api: DangerZoneApi
) : DangerZoneRepository {

    override suspend fun getDangerZones(): List<DangerZone> {
        return api.getDangerZones().map { it.toDomain() }
    }
}