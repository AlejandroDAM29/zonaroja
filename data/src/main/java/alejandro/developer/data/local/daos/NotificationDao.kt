package alejandro.developer.data.local.daos

import alejandro.developer.data.local.entities.NotificationEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query(
        "SELECT * FROM notifications WHERE userId = :userId ORDER BY receivedAt DESC, id DESC"
    )
    fun observeNotifications(userId: String): Flow<List<NotificationEntity>>

    @Query(
        "SELECT * FROM notifications WHERE id = :notificationId AND userId = :userId LIMIT 1"
    )
    fun observeNotification(notificationId: Long, userId: String): Flow<NotificationEntity?>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun observeUnreadNotificationsCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId AND userId = :userId")
    suspend fun markAsRead(notificationId: Long, userId: String)

    @Query("DELETE FROM notifications WHERE id = :notificationId AND userId = :userId")
    suspend fun deleteNotification(notificationId: Long, userId: String)
}
