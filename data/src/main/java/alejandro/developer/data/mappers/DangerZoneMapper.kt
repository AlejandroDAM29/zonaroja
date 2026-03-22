package alejandro.developer.data.mappers

import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.local.relations.DangerZoneWithPoints
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.GeoPoint

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

fun DangerZoneModel.toEntity(userId: String): DangerZoneEntity {

    return DangerZoneEntity(
        zoneId = id,
        userId = userId,
        zoneName = zoneName,
        city = city,
        riskLevel = riskLevel,
        povertyRiskRate = povertyRiskRate,
        unemploymentRate = unemploymentRate,
        priceSquareMeter = priceSquareMeter
    )
}

fun DangerZoneModel.toGeoEntities(dangerZoneLocalId: Long): List<GeoPointEntity> {

    return points.map {
        GeoPointEntity(
            dangerZoneLocalId = dangerZoneLocalId,
            lat = it.lat,
            lng = it.lng,
            order = it.order
        )
    }
}

fun DangerZoneWithPoints.toDomain(): DangerZoneModel {
    return DangerZoneModel(
        id = zone.zoneId,
        zoneName = zone.zoneName,
        city = zone.city,
        points = points.map { it.toDomain() }.sortedBy(GeoPoint::order),
        riskLevel = zone.riskLevel,
        povertyRiskRate = zone.povertyRiskRate,
        unemploymentRate = zone.unemploymentRate,
        priceSquareMeter = zone.priceSquareMeter
    )
}

fun GeoPointEntity.toDomain(): GeoPoint {
    return GeoPoint(
        lat = lat,
        lng = lng,
        order = order
    )
}
