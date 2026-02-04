package alejandro.developer.data.remote.datasources

import alejandro.developer.data.remote.apis.GetCiudadesApi
import javax.inject.Inject

class CiudadesRemoteDataSource @Inject constructor(
    private val api: GetCiudadesApi
) {
    suspend fun getCiudades(): List<String> =
        api.getCiudades()
}
