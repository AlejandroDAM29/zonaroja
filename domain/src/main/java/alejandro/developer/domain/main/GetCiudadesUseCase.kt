package alejandro.developer.domain.main

import javax.inject.Inject

class GetCiudadesUseCase @Inject constructor(
    private val repository: CiudadesRepository
) {
    suspend operator fun invoke(): List<String> =
        repository.getCiudades()
}