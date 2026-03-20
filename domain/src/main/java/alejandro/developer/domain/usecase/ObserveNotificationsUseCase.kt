package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.repositories.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<List<AppNotificationModel>> {
        return repository.observeNotifications()
    }
}
