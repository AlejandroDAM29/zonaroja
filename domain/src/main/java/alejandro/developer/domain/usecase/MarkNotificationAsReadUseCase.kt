package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkNotificationAsReadUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: Long) {
        repository.markAsRead(notificationId)
    }
}
