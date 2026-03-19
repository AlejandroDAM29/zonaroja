package alejandro.developer.domain.repositories

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.UserPreferencesModel
import kotlinx.coroutines.flow.Flow

interface UserSettingsRepository {
    fun observeSettings(): Flow<UserPreferencesModel>

    suspend fun getSettings(): UserPreferencesModel

    suspend fun setDarkThemeEnabled(enabled: Boolean)

    suspend fun setSelectedCurrency(currency: AppCurrency)

    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun syncNotificationSubscriptions()
}
