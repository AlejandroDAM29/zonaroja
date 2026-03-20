package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.local.datasources.NotificationLocalDataSource
import alejandro.developer.data.mappers.toDomain
import alejandro.developer.data.mappers.toEntity
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.repositories.NotificationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl @Inject constructor(
    private val local: NotificationLocalDataSource
) : NotificationRepository {

    override fun observeNotifications(): Flow<List<AppNotificationModel>> {
        return local.observeNotifications().map { notifications ->
            notifications.map { it.toDomain() }
        }
    }

    override fun observeUnreadNotificationsCount(): Flow<Int> {
        return local.observeUnreadNotificationsCount()
    }

    override fun observeNotification(notificationId: Long): Flow<AppNotificationModel?> {
        return local.observeNotification(notificationId).map { notification ->
            notification?.toDomain()
        }
    }

    override suspend fun saveNotification(notification: IncomingNotificationModel): Long {
        return local.insertNotification(notification.toEntity())
    }

    override suspend fun markAsRead(notificationId: Long) {
        local.markAsRead(notificationId)
    }

    override suspend fun deleteNotification(notificationId: Long) {
        local.deleteNotification(notificationId)
    }
}
