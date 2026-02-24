package alejandro.developer.data.remote.apis

import alejandro.developer.data.remote.dto.DangerZoneDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DangerZoneApi {

    @GET("getZonasRojas.php")
    suspend fun getDangerZones(
        @Query("minLat") minLat: Double,
        @Query("maxLat") maxLat: Double,
        @Query("minLng") minLng: Double,
        @Query("maxLng") maxLng: Double
    ): List<DangerZoneDto>
}