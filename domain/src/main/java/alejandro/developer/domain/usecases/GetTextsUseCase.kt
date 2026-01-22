package alejandro.developer.domain.usecases

import alejandro.developer.domain.repositories.TextRepository
import javax.inject.Inject

class GetTextsUseCase @Inject constructor(
    private val repository: TextRepository
) {
    suspend operator fun invoke(): List<String> =
        repository.getTexts()
}