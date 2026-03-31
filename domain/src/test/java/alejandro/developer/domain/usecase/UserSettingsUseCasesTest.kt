package alejandro.developer.domain.usecase

import alejandro.developer.domain.sampleUserPreferencesModel
import alejandro.developer.domain.repositories.UserSettingsRepository
import alejandro.developer.domain.models.AppCurrency
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UserSettingsUseCasesTest {

    private val repository: UserSettingsRepository = mock()

    @Test
    fun observeUserPreferences_returnsRepositoryFlow() {
        val expected = flowOf(sampleUserPreferencesModel())
        whenever(repository.observeSettings()).thenReturn(expected)

        val result = ObserveUserPreferencesUseCase(repository).invoke()

        assertSame(expected, result)
        verify(repository).observeSettings()
    }

    @Test
    fun getUserPreferences_returnsRepositoryResult() = runTest {
        val expected = sampleUserPreferencesModel()
        whenever(repository.getSettings()).thenReturn(expected)

        val result = GetUserPreferencesUseCase(repository).invoke()

        assertEquals(expected, result)
        verify(repository).getSettings()
    }

    @Test
    fun setDarkThemeEnabled_delegatesToRepository() = runTest {
        SetDarkThemeEnabledUseCase(repository).invoke(true)

        verify(repository).setDarkThemeEnabled(true)
    }

    @Test
    fun setNotificationsEnabled_delegatesToRepository() = runTest {
        SetNotificationsEnabledUseCase(repository).invoke(false)

        verify(repository).setNotificationsEnabled(false)
    }

    @Test
    fun setSelectedCurrency_delegatesToRepository() = runTest {
        SetSelectedCurrencyUseCase(repository).invoke(AppCurrency.MXN)

        verify(repository).setSelectedCurrency(AppCurrency.MXN)
    }

    @Test
    fun syncNotificationSubscriptions_delegatesToRepository() = runTest {
        SyncNotificationSubscriptionsUseCase(repository).invoke()

        verify(repository).syncNotificationSubscriptions()
    }
}
