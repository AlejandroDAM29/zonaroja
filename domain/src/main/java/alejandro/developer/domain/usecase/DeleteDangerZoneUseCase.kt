package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.DangerZoneRepository
import javax.inject.Inject

class DeleteDangerZoneUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.deleteDangerZoneLocal(id)
    }
}