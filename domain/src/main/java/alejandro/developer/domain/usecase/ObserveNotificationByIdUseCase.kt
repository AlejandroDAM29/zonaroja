package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.repositories.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveNotificationByIdUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(notificationId: Long): Flow<AppNotificationModel?> {
        return repository.observeNotification(notificationId)
    }
}
