package alejandro.developer.zonaroja.ui.screens.notifications

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotificationDateFormatterTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun formatNotificationListTimestamp_returnsOnlyTimeForToday() {
        val timestamp = Instant.parse("2026-03-30T16:46:00Z").toEpochMilli()
        android.os.SystemClock.setCurrentTimeMillis(timestamp)
        val expectedTime = android.text.format.DateFormat.getTimeFormat(context).format(Date(timestamp))

        val result = formatNotificationListTimestamp(context, timestamp)

        assertEquals(expectedTime, result)
    }

    @Test
    fun formatNotificationListTimestamp_includesDateForPastDays() {
        val timestamp = Instant.parse("2025-01-10T15:30:00Z").toEpochMilli()
        val expectedDate = android.text.format.DateFormat.getMediumDateFormat(context).format(Date(timestamp))
        val expectedTime = android.text.format.DateFormat.getTimeFormat(context).format(Date(timestamp))

        val result = formatNotificationListTimestamp(context, timestamp)

        assertTrue(result.contains(expectedDate))
        assertTrue(result.contains(expectedTime))
    }

    @Test
    fun formatNotificationDateTime_alwaysIncludesDateAndTime() {
        val timestamp = Instant.parse("2025-02-14T08:45:00Z").toEpochMilli()
        val expectedDate = android.text.format.DateFormat.getMediumDateFormat(context).format(Date(timestamp))
        val expectedTime = android.text.format.DateFormat.getTimeFormat(context).format(Date(timestamp))

        val result = formatNotificationDateTime(context, timestamp)

        assertTrue(result.contains(expectedDate))
        assertTrue(result.contains(expectedTime))
    }
}
