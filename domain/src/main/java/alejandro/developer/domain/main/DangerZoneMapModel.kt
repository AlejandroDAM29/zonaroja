package alejandro.developer.domain.main

data class DangerZone(
    val id: Int,
    val zoneName: String,
    val city: String,
    val points: List<GeoPoint>,
    val riskLevel: RiskLevel
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