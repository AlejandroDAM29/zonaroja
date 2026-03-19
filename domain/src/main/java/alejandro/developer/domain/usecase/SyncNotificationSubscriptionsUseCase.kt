package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.UserSettingsRepository
import javax.inject.Inject

class SyncNotificationSubscriptionsUseCase @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) {
    suspend operator fun invoke() {
        userSettingsRepository.syncNotificationSubscriptions()
    }
}
