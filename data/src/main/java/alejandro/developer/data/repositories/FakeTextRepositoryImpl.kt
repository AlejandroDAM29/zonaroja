package alejandro.developer.data.repositories

import alejandro.developer.domain.repositories.TextRepository
import jakarta.inject.Inject

class FakeTextRepositoryImpl @Inject constructor() : TextRepository {

    override suspend fun getTexts(): List<String> {
        return listOf(
            "Zona Roja",
            "Barrio Seguro",
            "Alta Peligrosidad",
            "Baja Peligrosidad"
        )
    }
}