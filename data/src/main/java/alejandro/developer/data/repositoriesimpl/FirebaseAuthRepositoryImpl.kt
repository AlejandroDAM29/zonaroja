package alejandro.developer.data.repositoriesimpl

import alejandro.developer.domain.repositories.AuthRepository
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit> = runCatching {
        firebaseAuth
            .createUserWithEmailAndPassword(email, password)
            .await()
    }

    override suspend fun loginWithGoogle(
        idToken: String
    ): Result<Unit> =
        runCatching {
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
        firebaseAuth
            .sendPasswordResetEmail(email)
            .await()
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        firebaseAuth.currentUser?.delete()?.await()
            ?: error("No hay ningun usuario autenticado")
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }
}
