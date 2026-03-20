package alejandro.developer.domain.models

data class IncomingNotificationModel(
    val remoteMessageId: String?,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val receivedAt: Long
)
