package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DangerZoneDto(

    val id: Int,

    @param:Json(name = "nombre")
    val zoneName: String,

    @param:Json(name = "ciudad")
    val city: String,

    @param:Json(name = "nivel_riesgo")
    val riskLevel: String,

    @param:Json(name = "coordenadas")
    val points: List<GeoPointDto>,

    @param:Json(name = "riesgo_pobreza")
    val povertyRiskRate: Double,

    @param:Json(name = "tasa_paro")
    val unemploymentRate: Double,

    @param:Json(name = "precio_metro_cuadrado")
    val priceSquareMeter: Int
)

@JsonClass(generateAdapter = true)
data class GeoPointDto(
    val lat: Double,
    val lng: Double,
    val order: Int
)
