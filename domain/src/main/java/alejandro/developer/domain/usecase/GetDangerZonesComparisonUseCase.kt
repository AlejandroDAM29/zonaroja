package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.repositories.DangerZoneRepository
import javax.inject.Inject

class GetDangerZonesComparisonUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {
    suspend operator fun invoke(): List<DangerZoneComparisonModel> {
        return repository.getDangerZonesForComparisonRemote()
    }
}
