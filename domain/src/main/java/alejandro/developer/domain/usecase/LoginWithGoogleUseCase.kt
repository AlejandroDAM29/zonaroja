package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> =
        authRepository.loginWithGoogle(idToken)
}
