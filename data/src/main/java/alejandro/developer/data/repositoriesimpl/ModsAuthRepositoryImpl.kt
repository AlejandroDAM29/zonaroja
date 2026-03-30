package alejandro.developer.data.repositoriesimpl

import alejandro.developer.domain.repositories.AuthRepository
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class ModsAuthRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : AuthRepository {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val currentSession = MutableStateFlow(loadStoredSession())

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        persistSession(
            email = email,
            isPasswordProvider = true
        )
        Unit
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        persistSession(
            email = email,
            isPasswordProvider = true
        )
        Unit
    }

    override suspend fun loginWithGoogle(
        idToken: String
    ): Result<Unit> = runCatching {
        persistSession(
            email = MOCK_GOOGLE_EMAIL,
            isPasswordProvider = false
        )
        Unit
    }

    override suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> = Result.success(Unit)

    override fun getCurrentUserEmail(): String? {
        return currentSession.value?.email
    }

    override fun getCurrentUserId(): String? {
        return currentSession.value?.userId
    }

    override fun observeCurrentUserId(): Flow<String?> {
        return currentSession
            .map { it?.userId }
            .distinctUntilChanged()
    }

    override fun isCurrentUserPasswordProvider(): Boolean {
        return currentSession.value?.isPasswordProvider == true
    }

    override fun isUserLoggedIn(): Boolean {
        return currentSession.value != null
    }

    override suspend fun reauthenticateWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        check(currentSession.value != null) { "No authenticated user" }
        Unit
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        clearSession()
        Unit
    }

    override suspend fun logout() {
        clearSession()
    }

    private fun loadStoredSession(): ModsAuthSession? {
        val userId = preferences.getString(KEY_USER_ID, null) ?: return null
        val email = preferences.getString(KEY_EMAIL, MOCK_DEFAULT_EMAIL) ?: MOCK_DEFAULT_EMAIL
        val isPasswordProvider = preferences.getBoolean(KEY_IS_PASSWORD_PROVIDER, true)

        return ModsAuthSession(
            userId = userId,
            email = email,
            isPasswordProvider = isPasswordProvider
        )
    }

    private fun persistSession(
        email: String,
        isPasswordProvider: Boolean
    ) {
        val normalizedEmail = email.trim().ifBlank {
            if (isPasswordProvider) MOCK_DEFAULT_EMAIL else MOCK_GOOGLE_EMAIL
        }

        val session = ModsAuthSession(
            userId = UUID.nameUUIDFromBytes(normalizedEmail.lowercase().toByteArray()).toString(),
            email = normalizedEmail,
            isPasswordProvider = isPasswordProvider
        )

        preferences.edit()
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_EMAIL, session.email)
            .putBoolean(KEY_IS_PASSWORD_PROVIDER, session.isPasswordProvider)
            .apply()

        currentSession.value = session
    }

    private fun clearSession() {
        preferences.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_EMAIL)
            .remove(KEY_IS_PASSWORD_PROVIDER)
            .apply()

        currentSession.value = null
    }

    private data class ModsAuthSession(
        val userId: String,
        val email: String,
        val isPasswordProvider: Boolean
    )

    private companion object {
        const val PREFERENCES_NAME = "mods_auth_session"
        const val KEY_USER_ID = "mock_user_id"
        const val KEY_EMAIL = "mock_user_email"
        const val KEY_IS_PASSWORD_PROVIDER = "mock_user_password_provider"
        const val MOCK_DEFAULT_EMAIL = "mods@zonaroja.local"
        const val MOCK_GOOGLE_EMAIL = "mods-google@zonaroja.local"
    }
}
