package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.repositories.DangerZoneRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedDangerZonesUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {
    operator fun invoke(): Flow<List<DangerZoneModel>> {
        return repository.observeSavedDangerZones()
    }
}
