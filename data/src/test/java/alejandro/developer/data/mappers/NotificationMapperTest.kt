package alejandro.developer.data.mappers

import alejandro.developer.data.sampleIncomingNotificationModel
import alejandro.developer.data.sampleNotificationEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class NotificationMapperTest {

    @Test
    fun entityToDomain_mapsAllFields() {
        val entity = sampleNotificationEntity()

        val result = entity.toDomain()

        assertEquals(entity.id, result.id)
        assertEquals(entity.title, result.title)
        assertEquals(entity.body, result.body)
        assertEquals(entity.imageUrl, result.imageUrl)
        assertEquals(entity.receivedAt, result.receivedAt)
        assertEquals(entity.isRead, result.isRead)
    }

    @Test
    fun incomingModelToEntity_setsUserAndUnreadByDefault() {
        val notification = sampleIncomingNotificationModel()

        val result = notification.toEntity("user-55")

        assertEquals("user-55", result.userId)
        assertEquals(notification.remoteMessageId, result.remoteMessageId)
        assertEquals(notification.title, result.title)
        assertEquals(notification.body, result.body)
        assertEquals(notification.imageUrl, result.imageUrl)
        assertEquals(notification.receivedAt, result.receivedAt)
        assertFalse(result.isRead)
    }
}
