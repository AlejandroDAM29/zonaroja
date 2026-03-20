package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long) {
        repository.deleteNotification(notificationId)
    }
}
