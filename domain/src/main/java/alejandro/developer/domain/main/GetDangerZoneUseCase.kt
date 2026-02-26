package alejandro.developer.domain.main

import javax.inject.Inject

class GetDangerZonesUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {
    suspend operator fun invoke(bounds: MapBounds): List<DangerZone> {
        return repository.getDangerZones(bounds = bounds)
    }
}