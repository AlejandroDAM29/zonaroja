package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.TestAuthRepository
import alejandro.developer.data.local.AppDatabase
import alejandro.developer.data.local.datasources.NotificationLocalDataSource
import alejandro.developer.data.testIncomingNotification
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotificationRepositoryRoomIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var authRepository: TestAuthRepository
    private lateinit var repository: NotificationRepositoryImpl

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        authRepository = TestAuthRepository(initialUserId = "notifications-user-1")
        repository = NotificationRepositoryImpl(
            local = NotificationLocalDataSource(database.notificationDao()),
            authRepository = authRepository
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun observeNotifications_switchesWithActiveUserScope() = runTest {
        val firstNotificationId = repository.saveNotification(
            testIncomingNotification(
                title = "Usuario 1",
                remoteMessageId = "remote-user-1"
            )
        )

        authRepository.setCurrentUserId("notifications-user-2")
        repository.saveNotification(
            testIncomingNotification(
                title = "Usuario 2",
                remoteMessageId = "remote-user-2",
                receivedAt = 1_700_000_000_100
            )
        )

        authRepository.setCurrentUserId("notifications-user-1")

        repository.observeNotifications().test {
            val firstUserItems = awaitItem()
            assertEquals(1, firstUserItems.size)
            assertEquals(firstNotificationId, firstUserItems.first().id)
            assertEquals("Usuario 1", firstUserItems.first().title)

            authRepository.setCurrentUserId("notifications-user-2")

            val secondUserItems = awaitItem()
            assertEquals(1, secondUserItems.size)
            assertEquals("Usuario 2", secondUserItems.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun unreadCount_and_delete_are_backedByRoomUpdates() = runTest {
        val firstNotificationId = repository.saveNotification(
            testIncomingNotification(
                title = "Pendiente 1",
                remoteMessageId = "pending-1"
            )
        )
        val secondNotificationId = repository.saveNotification(
            testIncomingNotification(
                title = "Pendiente 2",
                remoteMessageId = "pending-2",
                receivedAt = 1_700_000_000_200
            )
        )

        repository.observeUnreadNotificationsCount().test {
            assertEquals(2, awaitItem())

            repository.markAsRead(firstNotificationId)
            assertEquals(1, awaitItem())

            repository.deleteNotification(secondNotificationId)
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        repository.observeNotifications().test {
            val remainingNotifications = awaitItem()
            assertEquals(1, remainingNotifications.size)
            assertTrue(remainingNotifications.first().isRead)
            assertEquals(firstNotificationId, remainingNotifications.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
