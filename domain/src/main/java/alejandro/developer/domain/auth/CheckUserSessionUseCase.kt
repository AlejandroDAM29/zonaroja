package alejandro.developer.domain.auth

import javax.inject.Inject

class CheckUserSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}
