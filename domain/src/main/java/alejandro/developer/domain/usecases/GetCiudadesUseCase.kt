package alejandro.developer.domain.usecases

import alejandro.developer.domain.repositories.CiudadesRepository
import javax.inject.Inject

class GetCiudadesUseCase @Inject constructor(
    private val repository: CiudadesRepository
) {
    suspend operator fun invoke(): List<String> =
        repository.getCiudades()
}