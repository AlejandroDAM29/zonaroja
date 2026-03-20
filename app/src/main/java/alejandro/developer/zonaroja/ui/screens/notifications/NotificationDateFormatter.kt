package alejandro.developer.zonaroja.ui.screens.notifications

import android.content.Context
import android.text.format.DateUtils
import java.util.Date

internal fun formatNotificationListTimestamp(
    context: Context,
    timestamp: Long
): String {
    val date = Date(timestamp)
    val timeText = android.text.format.DateFormat.getTimeFormat(context).format(date)

    return if (DateUtils.isToday(timestamp)) {
        timeText
    } else {
        val dateText = android.text.format.DateFormat.getMediumDateFormat(context).format(date)
        "$dateText · $timeText"
    }
}

internal fun formatNotificationDateTime(
    context: Context,
    timestamp: Long
): String {
    val date = Date(timestamp)
    val dateText = android.text.format.DateFormat.getMediumDateFormat(context).format(date)
    val timeText = android.text.format.DateFormat.getTimeFormat(context).format(date)
    return "$dateText · $timeText"
}
