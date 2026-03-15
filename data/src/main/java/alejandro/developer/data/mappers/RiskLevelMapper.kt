package alejandro.developer.data.mappers

import alejandro.developer.domain.models.RiskLevel

internal fun String.toRiskLevel(): RiskLevel {
    return when (uppercase()) {
        "LOW" -> RiskLevel.LOW
        "MEDIUM" -> RiskLevel.MEDIUM
        "HIGH" -> RiskLevel.HIGH
        else -> RiskLevel.LOW
    }
}
