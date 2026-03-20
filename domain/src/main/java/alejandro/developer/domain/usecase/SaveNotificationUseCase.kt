package alejandro.developer.domain.usecase

import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.repositories.NotificationRepository
import javax.inject.Inject

class SaveNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notification: IncomingNotificationModel): Long {
        return repository.saveNotification(notification)
    }
}
