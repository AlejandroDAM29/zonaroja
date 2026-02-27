package alejandro.developer.domain.usecase

import alejandro.developer.domain.repositories.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return repository.sendPasswordResetEmail(email)
    }
}