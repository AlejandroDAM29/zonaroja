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
    val points: List<GeoPointDto>,

    @Json(name = "riesgo_pobreza")
    val povertyRiskRate: Double,

    @Json(name = "tasa_paro")
    val unemploymentRate: Double,

    @Json(name = "precio_metro_cuadrado")
    val priceSquareMeter: Int
)

data class GeoPointDto(
    val lat: Double,
    val lng: Double,
    val order: Int
)