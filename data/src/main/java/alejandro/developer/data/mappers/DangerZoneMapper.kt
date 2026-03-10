package alejandro.developer.data.mappers

import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.RiskLevel

fun DangerZoneDto.toDomain(): DangerZoneModel {
    return DangerZoneModel(
        id = id,
        zoneName = zoneName,
        city = city,
        points = points.map { GeoPoint(
            lat = it.lat,
            lng = it.lng,
            order = it.order) },
        riskLevel = riskLevel.toRiskLevel(),
        povertyRiskRate = povertyRiskRate,
        unemploymentRate = unemploymentRate,
        priceSquareMeter = priceSquareMeter
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

fun DangerZoneModel.toEntity(): DangerZoneEntity {

    return DangerZoneEntity(
        id = id,
        zoneName = zoneName,
        city = city,
        riskLevel = riskLevel,
        povertyRiskRate = povertyRiskRate,
        unemploymentRate = unemploymentRate,
        priceSquareMeter = priceSquareMeter
    )
}

fun DangerZoneModel.toGeoEntities(): List<GeoPointEntity> {

    return points.map {
        GeoPointEntity(
            dangerZoneId = id,
            lat = it.lat,
            lng = it.lng,
            order = it.order
        )
    }
}