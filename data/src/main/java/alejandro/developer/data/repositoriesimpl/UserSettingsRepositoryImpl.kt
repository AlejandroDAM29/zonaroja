package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.session.toUserScopeKey
import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.repositories.AuthRepository
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val SETTINGS_DATASTORE_NAME = "user_settings"
private const val FCM_GENERAL_TOPIC = "zonaroja_general"

private val Context.userSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_DATASTORE_NAME
)

@Singleton
@OptIn(ExperimentalCoroutinesApi::class)
class UserSettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,
    private val authRepository: AuthRepository
) : UserSettingsRepository {

    override fun observeSettings(): Flow<UserPreferencesModel> {
        return authRepository.observeCurrentUserId().flatMapLatest { currentUserId ->
            val userScope = currentUserId.toUserScopeKey()
            context.userSettingsDataStore.data.map { preferences ->
                UserPreferencesModel(
                    darkThemeEnabled = preferences[Keys.darkThemeEnabled(userScope)] ?: false,
                    selectedCurrency = AppCurrency.fromCode(
                        preferences[Keys.selectedCurrency(userScope)] ?: AppCurrency.EUR.code
                    ),
                    notificationsEnabled = preferences[Keys.notificationsEnabled(userScope)] ?: false
                )
            }
        }
    }

    override suspend fun getSettings(): UserPreferencesModel {
        return observeSettings().first()
    }

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        val userScope = authRepository.getCurrentUserId().toUserScopeKey()
        context.userSettingsDataStore.edit { preferences ->
            preferences[Keys.darkThemeEnabled(userScope)] = enabled
        }
    }

    override suspend fun setSelectedCurrency(currency: AppCurrency) {
        val userScope = authRepository.getCurrentUserId().toUserScopeKey()
        context.userSettingsDataStore.edit { preferences ->
            preferences[Keys.selectedCurrency(userScope)] = currency.code
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        val userScope = authRepository.getCurrentUserId().toUserScopeKey()
        context.userSettingsDataStore.edit { preferences ->
            preferences[Keys.notificationsEnabled(userScope)] = enabled
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
        fun darkThemeEnabled(userScope: String) =
            booleanPreferencesKey("dark_theme_enabled_$userScope")

        fun notificationsEnabled(userScope: String) =
            booleanPreferencesKey("notifications_enabled_$userScope")

        fun selectedCurrency(userScope: String) =
            stringPreferencesKey("selected_currency_$userScope")
    }
}
