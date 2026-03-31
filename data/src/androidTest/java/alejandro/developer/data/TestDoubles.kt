package alejandro.developer.data

import alejandro.developer.data.subscriptions.NotificationSubscriptionManager
import alejandro.developer.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class TestAuthRepository(
    initialUserId: String? = null,
    private var currentUserEmail: String? = null
) : AuthRepository {

    private val currentUserIdFlow = MutableStateFlow(initialUserId)

    fun setCurrentUserId(userId: String?) {
        currentUserIdFlow.value = userId
    }

    fun setCurrentUserEmail(email: String?) {
        currentUserEmail = email
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<Unit> = Result.success(Unit)

    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> = Result.success(Unit)

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> = Result.success(Unit)

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = Result.success(Unit)

    override fun getCurrentUserEmail(): String? = currentUserEmail

    override fun getCurrentUserId(): String? = currentUserIdFlow.value

    override fun observeCurrentUserId(): Flow<String?> = currentUserIdFlow

    override fun isCurrentUserPasswordProvider(): Boolean = true

    override fun isUserLoggedIn(): Boolean = currentUserIdFlow.value != null

    override suspend fun reauthenticateWithEmail(email: String, password: String): Result<Unit> = Result.success(Unit)

    override suspend fun deleteCurrentUser(): Result<Unit> = Result.success(Unit)

    override suspend fun logout() {
        currentUserIdFlow.value = null
    }
}

internal class TestNotificationSubscriptionManager : NotificationSubscriptionManager {
    val events = mutableListOf<String>()

    override suspend fun subscribeToGeneralTopic() {
        events += "subscribe"
    }

    override suspend fun unsubscribeFromGeneralTopic() {
        events += "unsubscribe"
    }
}
