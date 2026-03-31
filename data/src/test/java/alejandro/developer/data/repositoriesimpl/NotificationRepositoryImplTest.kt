package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.local.datasources.NotificationLocalDataSource
import alejandro.developer.data.sampleIncomingNotificationModel
import alejandro.developer.data.sampleNotificationEntity
import alejandro.developer.data.session.GUEST_USER_SCOPE
import alejandro.developer.domain.repositories.AuthRepository
import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class NotificationRepositoryImplTest {

    private val local: NotificationLocalDataSource = mock()
    private val authRepository: AuthRepository = mock()

    private val repository = NotificationRepositoryImpl(local, authRepository)

    @Test
    fun observeNotifications_mapsEntitiesToDomain() = runTest {
        whenever(authRepository.observeCurrentUserId()).thenReturn(MutableStateFlow("user-1"))
        whenever(local.observeNotifications("user-1")).thenReturn(flowOf(listOf(sampleNotificationEntity())))

        repository.observeNotifications().test {
            val item = awaitItem()
            assertEquals(1, item.size)
            assertEquals("Aviso", item.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeUnreadNotificationsCount_delegatesUsingCurrentUserScope() = runTest {
        whenever(authRepository.observeCurrentUserId()).thenReturn(MutableStateFlow("user-1"))
        whenever(local.observeUnreadNotificationsCount("user-1")).thenReturn(flowOf(6))

        repository.observeUnreadNotificationsCount().test {
            assertEquals(6, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observeNotification_mapsSingleEntityToDomain() = runTest {
        whenever(authRepository.observeCurrentUserId()).thenReturn(MutableStateFlow("user-1"))
        whenever(local.observeNotification(22L, "user-1")).thenReturn(flowOf(sampleNotificationEntity()))

        repository.observeNotification(22L).test {
            assertEquals("Aviso", awaitItem()?.title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun saveNotification_usesGuestScopeWhenNoAuthenticatedUserExists() = runTest {
        val notification = sampleIncomingNotificationModel()
        whenever(authRepository.getCurrentUserId()).thenReturn(null)
        whenever(local.insertNotification(org.mockito.kotlin.any())).thenReturn(101L)

        val result = repository.saveNotification(notification)

        val entityCaptor = argumentCaptor<alejandro.developer.data.local.entities.NotificationEntity>()
        verify(local).insertNotification(entityCaptor.capture())
        assertEquals(GUEST_USER_SCOPE, entityCaptor.firstValue.userId)
        assertEquals(101L, result)
    }

    @Test
    fun markAsRead_delegatesWithUserScope() = runTest {
        whenever(authRepository.getCurrentUserId()).thenReturn("user-3")

        repository.markAsRead(7L)

        verify(local).markAsRead(7L, "user-3")
    }

    @Test
    fun deleteNotification_delegatesWithUserScope() = runTest {
        whenever(authRepository.getCurrentUserId()).thenReturn("user-3")

        repository.deleteNotification(8L)

        verify(local).deleteNotification(8L, "user-3")
    }
}
