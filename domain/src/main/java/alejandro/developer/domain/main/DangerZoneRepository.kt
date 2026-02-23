package alejandro.developer.domain.main

interface DangerZoneRepository {
    suspend fun getDangerZones(): List<DangerZone>
}