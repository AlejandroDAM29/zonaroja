package alejandro.developer.data.repositoriesimpl

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.requireInternet
import alejandro.developer.domain.repositories.AuthRepository
import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val networkMonitor: NetworkMonitor
) : AuthRepository {

    private fun resolveCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    private fun resolveCurrentUserEmail(): String? {
        val user = firebaseAuth.currentUser ?: return null
        return user.email
            ?: user.providerData.firstNotNullOfOrNull { provider -> provider.email }
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        networkMonitor.requireInternet()
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        networkMonitor.requireInternet()
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
    }

    override suspend fun loginWithGoogle(
        idToken: String
    ): Result<Unit> =
        runCatching {
            networkMonitor.requireInternet()
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential)
                .addOnFailureListener { e ->
                    Log.e("Google error:", e.message.toString())
                }
                .await()
        }

    override suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit> = runCatching {
        networkMonitor.requireInternet()
        firebaseAuth
            .sendPasswordResetEmail(email)
            .await()
    }

    override fun getCurrentUserEmail(): String? {
        return resolveCurrentUserEmail()
    }

    override fun getCurrentUserId(): String? {
        return resolveCurrentUserId()
    }

    override fun observeCurrentUserId(): Flow<String?> {
        return callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                trySend(auth.currentUser?.uid)
            }

            firebaseAuth.addAuthStateListener(listener)
            trySend(resolveCurrentUserId())

            awaitClose {
                firebaseAuth.removeAuthStateListener(listener)
            }
        }.distinctUntilChanged()
    }

    override fun isCurrentUserPasswordProvider(): Boolean {
        return firebaseAuth.currentUser
            ?.providerData
            ?.any { it.providerId == EmailAuthProvider.PROVIDER_ID } == true
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun reauthenticateWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        networkMonitor.requireInternet()
        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("No authenticated user")
        val resolvedEmail = email.ifBlank {
            resolveCurrentUserEmail()
                ?: throw IllegalStateException("No email associated with authenticated user")
        }
        val credential = EmailAuthProvider.getCredential(resolvedEmail, password)
        currentUser.reauthenticate(credential).await()
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        networkMonitor.requireInternet()
        val currentUser = firebaseAuth.currentUser
            ?: throw IllegalStateException("No authenticated user")
        currentUser.delete().await()
    }

    override suspend fun logout() {
        networkMonitor.requireInternet()
        firebaseAuth.signOut()
    }
}
