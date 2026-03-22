package alejandro.developer.data.local.datasources

import alejandro.developer.data.local.daos.NotificationDao
import alejandro.developer.data.local.entities.NotificationEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class NotificationLocalDataSource @Inject constructor(
    private val dao: NotificationDao
) {

    fun observeNotifications(userId: String): Flow<List<NotificationEntity>> {
        return dao.observeNotifications(userId)
    }

    fun observeNotification(notificationId: Long, userId: String): Flow<NotificationEntity?> {
        return dao.observeNotification(notificationId, userId)
    }

    fun observeUnreadNotificationsCount(userId: String): Flow<Int> {
        return dao.observeUnreadNotificationsCount(userId)
    }

    suspend fun insertNotification(notification: NotificationEntity): Long {
        return dao.insertNotification(notification)
    }

    suspend fun markAsRead(notificationId: Long, userId: String) {
        dao.markAsRead(notificationId, userId)
    }

    suspend fun deleteNotification(notificationId: Long, userId: String) {
        dao.deleteNotification(notificationId, userId)
    }
}
