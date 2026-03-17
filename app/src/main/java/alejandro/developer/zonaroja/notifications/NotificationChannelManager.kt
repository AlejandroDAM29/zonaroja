package alejandro.developer.zonaroja.notifications

import alejandro.developer.zonaroja.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

object NotificationChannelManager {
    fun ensureGeneralChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService<NotificationManager>() ?: return
        val channel = NotificationChannel(
            GENERAL_CHANNEL_ID,
            context.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.default_notification_channel_description)
        }

        notificationManager.createNotificationChannel(channel)
    }

    const val GENERAL_CHANNEL_ID = "zonaroja_general_channel"
}
