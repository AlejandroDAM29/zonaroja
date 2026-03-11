package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.DangerZoneRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedZonesUseCase @Inject constructor(
    private val repository: DangerZoneRepository
) {
    operator fun invoke(): Flow<List<Int>> {
        return repository.getSavedZoneIds()
    }
}