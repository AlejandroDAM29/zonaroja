package alejandro.developer.data

import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.local.entities.NotificationEntity
import alejandro.developer.data.local.relations.DangerZoneWithPoints
import alejandro.developer.data.remote.dto.DangerZoneComparisonDto
import alejandro.developer.data.remote.dto.DangerZoneDto
import alejandro.developer.data.remote.dto.DemographyItemDto
import alejandro.developer.data.remote.dto.EconomyStatsDto
import alejandro.developer.data.remote.dto.GeoPointDto
import alejandro.developer.data.remote.dto.SocietyStatsDto
import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.models.AppCurrency

internal fun sampleDangerZoneDto() = DangerZoneDto(
    id = 12,
    zoneName = "Lavapies",
    city = "Madrid",
    riskLevel = "medium",
    points = listOf(
        GeoPointDto(lat = 40.4, lng = -3.7, order = 2),
        GeoPointDto(lat = 40.3, lng = -3.8, order = 1)
    ),
    povertyRiskRate = 14.2,
    unemploymentRate = 8.1,
    priceSquareMeter = 3100
)

internal fun sampleDangerZoneComparisonDto() = DangerZoneComparisonDto(
    id = 45,
    zoneName = "Vallecas",
    city = "Madrid",
    riskLevel = "HIGH",
    povertyRiskRate = 18.6,
    unemploymentRate = 10.3,
    priceSquareMeter = 2600
)

internal fun sampleDangerZoneModel() = DangerZoneModel(
    id = 12,
    zoneName = "Lavapies",
    city = "Madrid",
    points = listOf(
        GeoPoint(lat = 40.4, lng = -3.7, order = 2),
        GeoPoint(lat = 40.3, lng = -3.8, order = 1)
    ),
    riskLevel = RiskLevel.MEDIUM,
    povertyRiskRate = 14.2,
    unemploymentRate = 8.1,
    priceSquareMeter = 3100
)

internal fun sampleDangerZoneEntity() = DangerZoneEntity(
    localId = 7L,
    zoneId = 12,
    userId = "user-1",
    zoneName = "Lavapies",
    city = "Madrid",
    riskLevel = RiskLevel.MEDIUM,
    povertyRiskRate = 14.2,
    unemploymentRate = 8.1,
    priceSquareMeter = 3100
)

internal fun sampleGeoPointEntities(localId: Long = 7L) = listOf(
    GeoPointEntity(dangerZoneLocalId = localId, lat = 40.4, lng = -3.7, order = 2),
    GeoPointEntity(dangerZoneLocalId = localId, lat = 40.3, lng = -3.8, order = 1)
)

internal fun sampleDangerZoneWithPoints() = DangerZoneWithPoints(
    zone = sampleDangerZoneEntity(),
    points = sampleGeoPointEntities()
)

internal fun sampleEconomyStatsDto() = EconomyStatsDto(
    hoodRent = 900,
    cityRent = 1200,
    hoodPrice = 3200,
    cityPrice = 4100
)

internal fun sampleSocietyStatsDto() = SocietyStatsDto(
    hoodUnemployment = 10f,
    cityUnemployment = 8f,
    hoodPoberty = 17f,
    cityPoberty = 12f
)

internal fun sampleDemographyItemDtos() = listOf(
    DemographyItemDto(name = "18-25", percentage = 20f),
    DemographyItemDto(name = "26-35", percentage = 35f)
)

internal fun sampleStatsGraphicsDto() = StatsGraphicsDto(
    economy = sampleEconomyStatsDto(),
    society = sampleSocietyStatsDto(),
    demography = sampleDemographyItemDtos()
)

internal fun sampleNotificationEntity() = NotificationEntity(
    id = 9L,
    userId = "user-1",
    remoteMessageId = "remote-1",
    title = "Aviso",
    body = "Nueva alerta",
    imageUrl = "https://example.com/image.png",
    receivedAt = 1_700_000_000_000,
    isRead = true
)

internal fun sampleIncomingNotificationModel() = IncomingNotificationModel(
    remoteMessageId = "remote-1",
    title = "Aviso",
    body = "Nueva alerta",
    imageUrl = "https://example.com/image.png",
    receivedAt = 1_700_000_000_000
)

internal fun sampleAppNotificationModel() = AppNotificationModel(
    id = 9L,
    title = "Aviso",
    body = "Nueva alerta",
    imageUrl = "https://example.com/image.png",
    receivedAt = 1_700_000_000_000,
    isRead = true
)

internal fun sampleFeatureFlagsModel(enabled: Boolean = true) = FeatureFlagsModel(
    googleLoginEnabled = enabled
)

internal fun sampleUserPreferencesModel() = UserPreferencesModel(
    darkThemeEnabled = true,
    selectedCurrency = AppCurrency.GBP,
    notificationsEnabled = true
)
