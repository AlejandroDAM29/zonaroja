package alejandro.developer.domain.main

interface LocationSearchRepository {
    suspend fun searchCity(query: String): Pair<Double, Double>?
}