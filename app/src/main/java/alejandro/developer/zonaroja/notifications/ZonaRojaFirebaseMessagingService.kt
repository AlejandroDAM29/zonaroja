package alejandro.developer.zonaroja.notifications

import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.usecase.GetUserPreferencesUseCase
import alejandro.developer.domain.usecase.SaveNotificationUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.MainActivity
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ZonaRojaFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var getUserPreferencesUseCase: GetUserPreferencesUseCase

    @Inject
    lateinit var syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase

    @Inject
    lateinit var saveNotificationUseCase: SaveNotificationUseCase

    override fun onCreate() {
        super.onCreate()
        NotificationChannelManager.ensureGeneralChannel(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        runBlocking {
            syncNotificationSubscriptionsUseCase()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val userPreferences = runBlocking { getUserPreferencesUseCase() }
        if (!userPreferences.notificationsEnabled) return

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.app_name)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: return
        val imageUrl = message.notification?.imageUrl?.toString()
            ?: message.data["imageUrl"]
            ?: message.data["image"]
        val receivedAt = message.sentTime.takeIf { it > 0L } ?: System.currentTimeMillis()

        val storedNotificationId = runBlocking {
            saveNotificationUseCase(
                IncomingNotificationModel(
                    remoteMessageId = message.messageId,
                    title = title,
                    body = body,
                    imageUrl = imageUrl,
                    receivedAt = receivedAt
                )
            )
        }

        if (!hasNotificationPermission()) return

        val pendingIntent = PendingIntent.getActivity(
            this,
            storedNotificationId.toInt(),
            NotificationNavigationContract.attachStoredNotificationId(
                Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                storedNotificationId
            ),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this,
            NotificationChannelManager.GENERAL_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(RedZoneColor.toArgb())
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).notify(
            storedNotificationId.toInt(),
            notification
        )
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
