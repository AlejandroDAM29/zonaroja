package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.UserSettingsRepository
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        userSettingsRepository.setNotificationsEnabled(enabled)
    }
}
