package alejandro.developer.domain.main

interface DangerZoneRepository {
    suspend fun getDangerZones(bounds: MapBounds): List<DangerZone>
}