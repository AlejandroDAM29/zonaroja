package alejandro.developer.core.auth

import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseTokenProvider @Inject constructor() {

    suspend fun getToken(forceRefresh: Boolean = false): String {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IllegalStateException("User not authenticated")

        return user.getIdToken(forceRefresh).await().token
            ?: throw IllegalStateException("Token null")
    }
}
