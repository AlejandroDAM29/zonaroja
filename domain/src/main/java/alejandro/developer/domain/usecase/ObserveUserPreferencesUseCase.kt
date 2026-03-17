package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.repositories.UserSettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveUserPreferencesUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    operator fun invoke(): Flow<UserPreferencesModel> {
        return userSettingsRepository.observeSettings()
    }
}
