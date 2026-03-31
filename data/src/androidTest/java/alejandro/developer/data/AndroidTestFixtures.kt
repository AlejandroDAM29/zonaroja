package alejandro.developer.data

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.UserPreferencesModel

internal fun testIncomingNotification(
    title: String = "Aviso",
    body: String = "Nueva alerta",
    remoteMessageId: String = "remote-1",
    receivedAt: Long = 1_700_000_000_000
) = IncomingNotificationModel(
    remoteMessageId = remoteMessageId,
    title = title,
    body = body,
    imageUrl = "https://example.com/image.png",
    receivedAt = receivedAt
)

internal fun testDangerZone(
    id: Int = 12,
    zoneName: String = "Lavapies",
    city: String = "Madrid"
) = DangerZoneModel(
    id = id,
    zoneName = zoneName,
    city = city,
    points = listOf(
        GeoPoint(lat = 40.4, lng = -3.7, order = 2),
        GeoPoint(lat = 40.3, lng = -3.8, order = 1)
    ),
    riskLevel = RiskLevel.MEDIUM,
    povertyRiskRate = 14.2,
    unemploymentRate = 8.1,
    priceSquareMeter = 3_100
)

internal fun testUserPreferences(
    darkThemeEnabled: Boolean = false,
    currency: AppCurrency = AppCurrency.EUR,
    notificationsEnabled: Boolean = false
) = UserPreferencesModel(
    darkThemeEnabled = darkThemeEnabled,
    selectedCurrency = currency,
    notificationsEnabled = notificationsEnabled
)
