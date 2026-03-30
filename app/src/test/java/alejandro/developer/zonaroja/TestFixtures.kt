package alejandro.developer.zonaroja

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.UserPreferencesModel

internal fun sampleNotificationModel() = AppNotificationModel(
    id = 15L,
    title = "Alerta",
    body = "Se ha detectado una incidencia",
    imageUrl = "https://example.com/alert.png",
    receivedAt = 1_700_000_000_000,
    isRead = false
)

internal fun sampleUserPreferencesModel(currency: AppCurrency = AppCurrency.EUR) = UserPreferencesModel(
    darkThemeEnabled = true,
    selectedCurrency = currency,
    notificationsEnabled = true
)

internal fun sampleFeatureFlagsModel(enabled: Boolean = true) = FeatureFlagsModel(
    googleLoginEnabled = enabled
)

internal fun sampleComparisonModel(
    id: Int,
    zoneName: String,
    city: String,
    riskLevel: RiskLevel,
    povertyRiskRate: Double,
    unemploymentRate: Double,
    priceSquareMeter: Int
) = DangerZoneComparisonModel(
    id = id,
    zoneName = zoneName,
    city = city,
    riskLevel = riskLevel,
    povertyRiskRate = povertyRiskRate,
    unemploymentRate = unemploymentRate,
    priceSquareMeter = priceSquareMeter
)
