package alejandro.developer.data.remote.apis

import alejandro.developer.data.remote.dto.DangerZoneDto
import retrofit2.http.GET

interface DangerZoneApi {

    @GET("getZonasRojas.php")
    suspend fun getDangerZones(): List<DangerZoneDto>
}