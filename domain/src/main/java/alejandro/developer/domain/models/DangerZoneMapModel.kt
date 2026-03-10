package alejandro.developer.domain.models

data class DangerZoneModel(
    val id: Int,
    val zoneName: String,
    val city: String,
    val points: List<GeoPoint>,
    val riskLevel: RiskLevel,
    val povertyRiskRate: Double,
    val unemploymentRate: Double,
    val priceSquareMeter: Int
)

data class GeoPoint(
    val lat: Double,
    val lng: Double,
    val order: Int
)

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}