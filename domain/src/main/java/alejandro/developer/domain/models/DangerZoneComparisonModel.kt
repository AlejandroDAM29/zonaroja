package alejandro.developer.domain.models

data class DangerZoneComparisonModel(
    val id: Int,
    val zoneName: String,
    val city: String,
    val riskLevel: RiskLevel,
    val povertyRiskRate: Double,
    val unemploymentRate: Double,
    val priceSquareMeter: Int
)
