package alejandro.developer.domain

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.SocietyStatsModel
import alejandro.developer.domain.models.StatsGraphicsModel
import alejandro.developer.domain.models.UserPreferencesModel

internal fun sampleMapBounds() = MapBounds(
    minLat = 40.0,
    maxLat = 41.0,
    minLng = -4.0,
    maxLng = -3.0
)

internal fun sampleDangerZoneModel() = DangerZoneModel(
    id = 10,
    zoneName = "Lavapies",
    city = "Madrid",
    points = listOf(
        GeoPoint(lat = 40.1, lng = -3.7, order = 1),
        GeoPoint(lat = 40.2, lng = -3.6, order = 2)
    ),
    riskLevel = RiskLevel.MEDIUM,
    povertyRiskRate = 17.5,
    unemploymentRate = 8.2,
    priceSquareMeter = 3200
)

internal fun sampleDangerZoneComparisonModel() = DangerZoneComparisonModel(
    id = 20,
    zoneName = "Tetuán",
    city = "Madrid",
    riskLevel = RiskLevel.HIGH,
    povertyRiskRate = 22.4,
    unemploymentRate = 11.7,
    priceSquareMeter = 2900
)

internal fun sampleStatsGraphicsModel() = StatsGraphicsModel(
    economy = EconomyStatsModel(
        hoodRent = 900,
        cityRent = 1200,
        hoodPrice = 3200,
        cityPrice = 4100
    ),
    demography = listOf(
        DemographyItemModel(name = "18-25", percentage = 20f),
        DemographyItemModel(name = "26-35", percentage = 35f)
    ),
    society = SocietyStatsModel(
        hoodUnemployment = 10f,
        cityUnemployment = 8f,
        hoodPoberty = 17f,
        cityPoberty = 12f
    )
)

internal fun sampleNotificationModel() = AppNotificationModel(
    id = 1L,
    title = "Aviso",
    body = "Nueva alerta",
    imageUrl = "https://example.com/image.png",
    receivedAt = 1_700_000_000_000,
    isRead = false
)

internal fun sampleIncomingNotificationModel() = IncomingNotificationModel(
    remoteMessageId = "remote-id",
    title = "Aviso",
    body = "Nueva alerta",
    imageUrl = "https://example.com/image.png",
    receivedAt = 1_700_000_000_000
)

internal fun sampleUserPreferencesModel() = UserPreferencesModel(
    darkThemeEnabled = true,
    selectedCurrency = AppCurrency.USD,
    notificationsEnabled = true
)
