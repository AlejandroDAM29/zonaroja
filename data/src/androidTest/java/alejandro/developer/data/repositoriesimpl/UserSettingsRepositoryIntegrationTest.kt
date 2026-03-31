package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.TestAuthRepository
import alejandro.developer.data.TestNotificationSubscriptionManager
import alejandro.developer.domain.models.AppCurrency
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserSettingsRepositoryIntegrationTest {

    private lateinit var authRepository: TestAuthRepository
    private lateinit var subscriptionManager: TestNotificationSubscriptionManager
    private lateinit var repository: UserSettingsRepositoryImpl
    private lateinit var firstUserId: String
    private lateinit var secondUserId: String

    @Before
    fun setUp() {
        val runId = System.nanoTime()
        firstUserId = "prefs-user-1-$runId"
        secondUserId = "prefs-user-2-$runId"
        authRepository = TestAuthRepository(
            initialUserId = firstUserId,
            currentUserEmail = "$firstUserId@example.com"
        )
        subscriptionManager = TestNotificationSubscriptionManager()
        repository = UserSettingsRepositoryImpl(
            context = ApplicationProvider.getApplicationContext(),
            notificationSubscriptionManager = subscriptionManager,
            authRepository = authRepository
        )
    }

    @Test
    fun preferences_are_scoped_per_user_and_syncTriggersSubscriptionChanges() = runTest {
        repository.setDarkThemeEnabled(true)
        repository.setSelectedCurrency(AppCurrency.USD)
        repository.setNotificationsEnabled(true)

        val firstUserSettings = repository.getSettings()
        assertEquals(true, firstUserSettings.darkThemeEnabled)
        assertEquals(AppCurrency.USD, firstUserSettings.selectedCurrency)
        assertEquals(true, firstUserSettings.notificationsEnabled)
        assertEquals(listOf("subscribe"), subscriptionManager.events)

        authRepository.setCurrentUserId(secondUserId)
        authRepository.setCurrentUserEmail("$secondUserId@example.com")

        val secondUserSettingsBeforeChanges = repository.getSettings()
        assertEquals(false, secondUserSettingsBeforeChanges.darkThemeEnabled)
        assertEquals(AppCurrency.EUR, secondUserSettingsBeforeChanges.selectedCurrency)
        assertEquals(false, secondUserSettingsBeforeChanges.notificationsEnabled)

        repository.setNotificationsEnabled(false)
        repository.setSelectedCurrency(AppCurrency.GBP)

        val secondUserSettingsAfterChanges = repository.getSettings()
        assertEquals(AppCurrency.GBP, secondUserSettingsAfterChanges.selectedCurrency)
        assertEquals(false, secondUserSettingsAfterChanges.notificationsEnabled)
        assertEquals(listOf("subscribe", "unsubscribe"), subscriptionManager.events)

        authRepository.setCurrentUserId(firstUserId)
        val restoredFirstUserSettings = repository.getSettings()
        assertEquals(true, restoredFirstUserSettings.darkThemeEnabled)
        assertEquals(AppCurrency.USD, restoredFirstUserSettings.selectedCurrency)
        assertEquals(true, restoredFirstUserSettings.notificationsEnabled)
    }
}
