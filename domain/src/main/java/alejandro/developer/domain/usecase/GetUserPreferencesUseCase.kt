package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.repositories.UserSettingsRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(): UserPreferencesModel {
        return userSettingsRepository.getSettings()
    }
}
