package alejandro.developer.domain.auth

import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        return authRepository.registerWithEmail(email, password)
    }
}

