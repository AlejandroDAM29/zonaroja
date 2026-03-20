package alejandro.developer.zonaroja.notifications

import alejandro.developer.domain.models.IncomingNotificationModel
import android.content.Intent

object NotificationNavigationContract {

    private const val EXTRA_OPEN_STORED_NOTIFICATION = "open_stored_notification"
    private const val EXTRA_STORED_NOTIFICATION_ID = "stored_notification_id"

    private val titleKeys = listOf(
        "title",
        "gcm.notification.title",
        "gcm.n.title"
    )

    private val bodyKeys = listOf(
        "body",
        "gcm.notification.body",
        "gcm.n.body"
    )

    private val imageKeys = listOf(
        "imageUrl",
        "image",
        "gcm.notification.image",
        "gcm.n.image",
        "google.c.a.image"
    )

    private val messageIdKeys = listOf(
        "google.message_id",
        "message_id"
    )

    private val receivedAtKeys = listOf(
        "receivedAt",
        "google.sent_time",
        "sent_time"
    )

    fun attachStoredNotificationId(
        intent: Intent,
        notificationId: Long
    ): Intent {
        return intent.apply {
            putExtra(EXTRA_OPEN_STORED_NOTIFICATION, true)
            putExtra(EXTRA_STORED_NOTIFICATION_ID, notificationId)
        }
    }

    fun consumeStoredNotificationId(intent: Intent?): Long? {
        if (intent == null) return null

        val notificationId = intent
            .takeIf { it.getBooleanExtra(EXTRA_OPEN_STORED_NOTIFICATION, false) }
            ?.getLongExtra(EXTRA_STORED_NOTIFICATION_ID, -1L)
            ?.takeIf { it > 0L }

        intent.removeExtra(EXTRA_OPEN_STORED_NOTIFICATION)
        intent.removeExtra(EXTRA_STORED_NOTIFICATION_ID)

        return notificationId
    }

    fun consumeIncomingNotification(
        intent: Intent?,
        fallbackTitle: String
    ): IncomingNotificationModel? {
        if (intent == null) return null

        val body = firstNotBlank(intent, bodyKeys) ?: return null
        val title = firstNotBlank(intent, titleKeys) ?: fallbackTitle
        val imageUrl = firstNotBlank(intent, imageKeys)
        val remoteMessageId = firstNotBlank(intent, messageIdKeys)
        val receivedAt = firstNotBlank(intent, receivedAtKeys)?.toLongOrNull()
            ?: System.currentTimeMillis()

        clearKeys(intent, titleKeys + bodyKeys + imageKeys + messageIdKeys + receivedAtKeys)

        return IncomingNotificationModel(
            remoteMessageId = remoteMessageId,
            title = title,
            body = body,
            imageUrl = imageUrl,
            receivedAt = receivedAt
        )
    }

    private fun firstNotBlank(
        intent: Intent,
        keys: List<String>
    ): String? {
        return keys.firstNotNullOfOrNull { key ->
            intent.getStringExtra(key)?.takeIf(String::isNotBlank)
        }
    }

    private fun clearKeys(
        intent: Intent,
        keys: List<String>
    ) {
        keys.forEach(intent::removeExtra)
    }
}
