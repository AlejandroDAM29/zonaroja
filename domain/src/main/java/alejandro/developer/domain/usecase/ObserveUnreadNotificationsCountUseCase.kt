package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.NotificationRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveUnreadNotificationsCountUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.observeUnreadNotificationsCount()
    }
}
