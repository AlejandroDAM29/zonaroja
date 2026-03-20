package alejandro.developer.data.local.datasources

import alejandro.developer.data.local.daos.NotificationDao
import alejandro.developer.data.local.entities.NotificationEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class NotificationLocalDataSource @Inject constructor(
    private val dao: NotificationDao
) {

    fun observeNotifications(): Flow<List<NotificationEntity>> {
        return dao.observeNotifications()
    }

    fun observeNotification(notificationId: Long): Flow<NotificationEntity?> {
        return dao.observeNotification(notificationId)
    }

    fun observeUnreadNotificationsCount(): Flow<Int> {
        return dao.observeUnreadNotificationsCount()
    }

    suspend fun insertNotification(notification: NotificationEntity): Long {
        return dao.insertNotification(notification)
    }

    suspend fun markAsRead(notificationId: Long) {
        dao.markAsRead(notificationId)
    }

    suspend fun deleteNotification(notificationId: Long) {
        dao.deleteNotification(notificationId)
    }
}
