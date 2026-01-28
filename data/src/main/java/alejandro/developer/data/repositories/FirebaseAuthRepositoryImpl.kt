package alejandro.developer.data.repositories

import alejandro.developer.domain.auth.AuthRepository
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
            firebaseAuth.signInWithCredential(credential).await()
        }
}
