package alejandro.developer.domain.main

interface CiudadesRepository {
   suspend fun getCiudades(): List<String>
}