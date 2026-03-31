package alejandro.developer.domain.usecase

import alejandro.developer.domain.sampleIncomingNotificationModel
import alejandro.developer.domain.sampleNotificationModel
import alejandro.developer.domain.repositories.NotificationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotificationUseCasesTest {

    private val repository: NotificationRepository = mock()

    @Test
    fun observeNotifications_returnsRepositoryFlow() {
        val expected = flowOf(listOf(sampleNotificationModel()))
        whenever(repository.observeNotifications()).thenReturn(expected)

        val result = ObserveNotificationsUseCase(repository).invoke()

        assertSame(expected, result)
        verify(repository).observeNotifications()
    }

    @Test
    fun observeUnreadNotificationsCount_returnsRepositoryFlow() {
        val expected = flowOf(4)
        whenever(repository.observeUnreadNotificationsCount()).thenReturn(expected)

        val result = ObserveUnreadNotificationsCountUseCase(repository).invoke()

        assertSame(expected, result)
        verify(repository).observeUnreadNotificationsCount()
    }

    @Test
    fun observeNotificationById_returnsRepositoryFlow() {
        val expected = flowOf(sampleNotificationModel())
        whenever(repository.observeNotification(55L)).thenReturn(expected)

        val result = ObserveNotificationByIdUseCase(repository).invoke(55L)

        assertSame(expected, result)
        verify(repository).observeNotification(55L)
    }

    @Test
    fun saveNotification_returnsRepositoryId() = runTest {
        val notification = sampleIncomingNotificationModel()
        whenever(repository.saveNotification(notification)).thenReturn(99L)

        val result = SaveNotificationUseCase(repository).invoke(notification)

        assertEquals(99L, result)
        verify(repository).saveNotification(notification)
    }

    @Test
    fun markNotificationAsRead_delegatesToRepository() = runTest {
        MarkNotificationAsReadUseCase(repository).invoke(88L)

        verify(repository).markAsRead(88L)
    }

    @Test
    fun deleteNotification_delegatesToRepository() = runTest {
        DeleteNotificationUseCase(repository).invoke(77L)

        verify(repository).deleteNotification(77L)
    }
}
