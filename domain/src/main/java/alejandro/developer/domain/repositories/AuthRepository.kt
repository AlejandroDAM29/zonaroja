package alejandro.developer.domain.repositories

interface AuthRepository {
    suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun registerWithEmail(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun loginWithGoogle(
        idToken: String
    ): Result<Unit>

    suspend fun sendPasswordResetEmail(
        email: String
    ): Result<Unit>

    fun isUserLoggedIn(): Boolean

    suspend fun logout()


}