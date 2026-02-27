package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.MapBounds

interface DangerZoneRepository {
    suspend fun getDangerZones(bounds: MapBounds): List<DangerZone>
}