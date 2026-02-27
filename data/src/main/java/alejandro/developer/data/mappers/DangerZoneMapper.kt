package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.RiskLevel

fun DangerZoneDto.toDomain(): DangerZone {
    return DangerZone(
        id = id,
        zoneName = zoneName,
        city = city,
        points = points.map { GeoPoint(
            lat = it.lat,
            lng = it.lng,
            order = it.order) },
        riskLevel = riskLevel.toRiskLevel()
    )
}

private fun String.toRiskLevel(): RiskLevel {
    return when(this.uppercase()) {
        "LOW" -> RiskLevel.LOW
        "MEDIUM" -> RiskLevel.MEDIUM
        "HIGH" -> RiskLevel.HIGH
        else -> RiskLevel.LOW
    }
}