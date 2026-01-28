package alejandro.developer.domain.auth

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
}