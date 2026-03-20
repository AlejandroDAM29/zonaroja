package alejandro.developer.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    indices = [
        Index(value = ["receivedAt"]),
        Index(value = ["isRead"])
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteMessageId: String?,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val receivedAt: Long,
    val isRead: Boolean = false
)
