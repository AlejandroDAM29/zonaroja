package alejandro.developer.data.remote.apis

import retrofit2.http.GET

interface GetCiudadesApi {

    @GET("getCiudades.php")
    suspend fun getCiudades(): List<String>
}