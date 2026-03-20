package alejandro.developer.domain.models

data class AppNotificationModel(
    val id: Long,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val receivedAt: Long,
    val isRead: Boolean
)
