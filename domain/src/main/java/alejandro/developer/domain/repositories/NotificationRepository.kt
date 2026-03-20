package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.IncomingNotificationModel
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun observeNotifications(): Flow<List<AppNotificationModel>>

    fun observeUnreadNotificationsCount(): Flow<Int>

    fun observeNotification(notificationId: Long): Flow<AppNotificationModel?>

    suspend fun saveNotification(notification: IncomingNotificationModel): Long

    suspend fun markAsRead(notificationId: Long)

    suspend fun deleteNotification(notificationId: Long)
}
