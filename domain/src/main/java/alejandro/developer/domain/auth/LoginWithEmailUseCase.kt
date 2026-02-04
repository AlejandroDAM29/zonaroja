package alejandro.developer.domain.auth

import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        return authRepository.loginWithEmail(email, password)
    }
}