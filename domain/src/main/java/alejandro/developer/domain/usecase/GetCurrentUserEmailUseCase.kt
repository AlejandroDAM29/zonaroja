package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.AuthRepository
import javax.inject.Inject

class GetCurrentUserEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String? {
        return authRepository.getCurrentUserEmail()
    }
}
