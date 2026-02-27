package alejandro.developer.domain.repositories

interface LocationSearchRepository {
    suspend fun searchCity(query: String): Pair<Double, Double>?
}