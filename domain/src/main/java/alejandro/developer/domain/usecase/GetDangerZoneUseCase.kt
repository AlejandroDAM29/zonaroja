package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.repositories.DangerZoneRepository
import javax.inject.Inject

class GetDangerZonesUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {
    suspend operator fun invoke(bounds: MapBounds): List<DangerZone> {
        return repository.getDangerZones(bounds = bounds)
    }
}