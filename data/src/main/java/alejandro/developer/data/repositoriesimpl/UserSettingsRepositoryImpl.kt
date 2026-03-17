package alejandro.developer.data.repositoriesimpl

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.repositories.UserSettingsRepository
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val SETTINGS_DATASTORE_NAME = "user_settings"
private const val FCM_GENERAL_TOPIC = "zonaroja_general"

private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME
)

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseMessaging: FirebaseMessaging
) : UserSettingsRepository {

    override fun observeSettings(): Flow<UserPreferencesModel> {
        return context.userSettingsDataStore.data.map { preferences ->
            UserPreferencesModel(
                darkThemeEnabled = preferences[Keys.DARK_THEME_ENABLED] ?: false,
                selectedCurrency = AppCurrency.fromCode(
                    preferences[Keys.SELECTED_CURRENCY] ?: AppCurrency.EUR.code
                ),
                notificationsEnabled = preferences[Keys.NOTIFICATIONS_ENABLED] ?: false
            )
        }
    }

    override suspend fun getSettings(): UserPreferencesModel {
        return observeSettings().first()
    }

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[Keys.DARK_THEME_ENABLED] = enabled
        }
    }

    override suspend fun setSelectedCurrency(currency: AppCurrency) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[Keys.SELECTED_CURRENCY] = currency.code
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userSettingsDataStore.edit { preferences ->
            preferences[Keys.NOTIFICATIONS_ENABLED] = enabled
        }
        syncNotificationSubscriptions()
    }

    override suspend fun syncNotificationSubscriptions() {
        val notificationsEnabled = getSettings().notificationsEnabled

        runCatching {
            if (notificationsEnabled) {
                firebaseMessaging.subscribeToTopic(FCM_GENERAL_TOPIC).await()
            } else {
                firebaseMessaging.unsubscribeFromTopic(FCM_GENERAL_TOPIC).await()
            }
        }
    }

    private object Keys {
        val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SELECTED_CURRENCY = stringPreferencesKey("selected_currency")
    }
}
