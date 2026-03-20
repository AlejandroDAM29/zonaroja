package alejandro.developer.data.mappers

import alejandro.developer.data.local.entities.NotificationEntity
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.IncomingNotificationModel

fun NotificationEntity.toDomain(): AppNotificationModel {
    return AppNotificationModel(
        id = id,
        title = title,
        body = body,
        imageUrl = imageUrl,
        receivedAt = receivedAt,
        isRead = isRead
    )
}

fun IncomingNotificationModel.toEntity(): NotificationEntity {
    return NotificationEntity(
        remoteMessageId = remoteMessageId,
        title = title,
        body = body,
        imageUrl = imageUrl,
        receivedAt = receivedAt,
        isRead = false
    )
}
