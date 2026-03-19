package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.UserSettingsRepository
import javax.inject.Inject

class SetDarkThemeEnabledUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        userSettingsRepository.setDarkThemeEnabled(enabled)
    }
}
