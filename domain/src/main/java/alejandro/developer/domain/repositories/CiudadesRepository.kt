package alejandro.developer.domain.repositories

interface CiudadesRepository {
   suspend fun getCiudades(): List<String>
}