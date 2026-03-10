package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.repositories.DangerZoneRepository
import javax.inject.Inject

class SaveDangerZoneUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {

    suspend operator fun invoke(zone: DangerZoneModel) {
        repository.saveDangerZoneLocal(zone)
    }
}