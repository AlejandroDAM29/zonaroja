package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.AuthRepository
import javax.inject.Inject

class DeleteCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.deleteCurrentUser()
    }
}
