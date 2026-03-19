package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.AuthRepository
import javax.inject.Inject

class ReauthenticateWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<Unit> {
        return authRepository.reauthenticateWithEmail(email, password)
    }
}
