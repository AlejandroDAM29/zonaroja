package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.domain.models.DangerZoneComparisonModel

fun DangerZoneComparisonDto.toDomain(): DangerZoneComparisonModel {
    return DangerZoneComparisonModel(
        id = id,
        zoneName = zoneName,
        city = city,
        riskLevel = riskLevel.toRiskLevel(),
        povertyRiskRate = povertyRiskRate,
        unemploymentRate = unemploymentRate,
        priceSquareMeter = priceSquareMeter
    )
}
