package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class DangerZoneDto(

    val id: Int,

    @Json(name = "nombre")
    val zoneName: String,

    @Json(name = "ciudad")
    val city: String,

    @Json(name = "nivel_riesgo")
    val riskLevel: String,

    @Json(name = "coordenadas")
    val points: List<GeoPointDto>
)

data class GeoPointDto(
    val lat: Double,
    val lng: Double,
    val order: Int
)