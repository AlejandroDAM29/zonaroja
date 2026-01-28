package alejandro.developer.domain.auth

import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<Unit> =
        authRepository.loginWithGoogle(idToken)
}
