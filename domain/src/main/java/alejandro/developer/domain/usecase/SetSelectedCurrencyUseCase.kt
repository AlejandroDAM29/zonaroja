package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.repositories.UserSettingsRepository
import javax.inject.Inject

class SetSelectedCurrencyUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke(currency: AppCurrency) {
        userSettingsRepository.setSelectedCurrency(currency)
    }
}
