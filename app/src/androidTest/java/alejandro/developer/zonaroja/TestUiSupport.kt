package alejandro.developer.zonaroja

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.models.AppNotificationModel
import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.models.GeoPoint
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.SocietyStatsModel
import alejandro.developer.domain.models.StatsGraphicsModel
import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.repositories.AuthRepository
import alejandro.developer.domain.repositories.DangerZoneRepository
import alejandro.developer.domain.repositories.FeatureFlagsRepository
import alejandro.developer.domain.repositories.GraphicsRepository
import alejandro.developer.domain.repositories.LocationSearchRepository
import alejandro.developer.domain.repositories.NotificationRepository
import alejandro.developer.domain.repositories.UserSettingsRepository
import alejandro.developer.zonaroja.ui.common.globalApp.AppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.theme.ZonarojaTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class RecordingAppUiController : AppUiController {
    val successMessages = mutableListOf<String>()
    val errorMessages = mutableListOf<String>()
    val warningMessages = mutableListOf<String>()

    override fun showSnackbarError(message: String) {
        errorMessages += message
    }

    override fun showSnackbarWarning(message: String) {
        warningMessages += message
    }

    override fun showSnackbarErrorWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit
    ) {
        errorMessages += message
    }

    override fun showSnackbarWarningWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit
    ) {
        warningMessages += message
    }

    override fun showSnackbarSuccess(message: String) {
        successMessages += message
    }
}

internal fun AndroidComposeTestRule<*, *>.setZonaRojaContent(
    appUiController: AppUiController = RecordingAppUiController(),
    content: @Composable () -> Unit
) {
    setContent {
        ZonarojaTheme(
            darkTheme = false,
            dynamicColor = false
        ) {
            CompositionLocalProvider(
                LocalAppUiController provides appUiController
            ) {
                content()
            }
        }
    }
}

internal class FakeAuthRepository : AuthRepository {
    var loginWithEmailResult: Result<Unit> = Result.success(Unit)
    var registerWithEmailResult: Result<Unit> = Result.success(Unit)
    var loginWithGoogleResult: Result<Unit> = Result.success(Unit)
    var sendPasswordResetEmailResult: Result<Unit> = Result.success(Unit)
    var reauthenticateResult: Result<Unit> = Result.success(Unit)
    var deleteCurrentUserResult: Result<Unit> = Result.success(Unit)
    var emailValue: String? = "user@example.com"
    var currentUserId = MutableStateFlow<String?>(null)

    override suspend fun loginWithEmail(email: String, password: String): Result<Unit> = loginWithEmailResult

    override suspend fun registerWithEmail(email: String, password: String): Result<Unit> = registerWithEmailResult

    override suspend fun loginWithGoogle(idToken: String): Result<Unit> = loginWithGoogleResult

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = sendPasswordResetEmailResult

    override fun getCurrentUserEmail(): String? = emailValue

    override fun getCurrentUserId(): String? = currentUserId.value

    override fun observeCurrentUserId(): Flow<String?> = currentUserId.asStateFlow()

    override fun isCurrentUserPasswordProvider(): Boolean = true

    override fun isUserLoggedIn(): Boolean = currentUserId.value != null

    override suspend fun reauthenticateWithEmail(email: String, password: String): Result<Unit> = reauthenticateResult

    override suspend fun deleteCurrentUser(): Result<Unit> = deleteCurrentUserResult

    override suspend fun logout() {
        currentUserId.value = null
    }
}

internal class FakeFeatureFlagsRepository(
    private val featureFlags: FeatureFlagsModel
) : FeatureFlagsRepository {
    override suspend fun getFeatureFlags(): FeatureFlagsModel = featureFlags
}

internal class FakeUserSettingsRepository : UserSettingsRepository {
    private val preferencesFlow = MutableStateFlow(UserPreferencesModel())
    var syncCalls = 0

    override fun observeSettings(): Flow<UserPreferencesModel> = preferencesFlow.asStateFlow()

    override suspend fun getSettings(): UserPreferencesModel = preferencesFlow.value

    override suspend fun setDarkThemeEnabled(enabled: Boolean) {
        preferencesFlow.value = preferencesFlow.value.copy(darkThemeEnabled = enabled)
    }

    override suspend fun setSelectedCurrency(currency: AppCurrency) {
        preferencesFlow.value = preferencesFlow.value.copy(selectedCurrency = currency)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        preferencesFlow.value = preferencesFlow.value.copy(notificationsEnabled = enabled)
    }

    override suspend fun syncNotificationSubscriptions() {
        syncCalls += 1
    }
}

internal class FakeNetworkMonitor(
    initialOnline: Boolean
) : NetworkMonitor {
    private val onlineState = MutableStateFlow(initialOnline)

    fun setOnline(value: Boolean) {
        onlineState.value = value
    }

    override val isOnline: Flow<Boolean> = onlineState.asStateFlow()

    override fun isCurrentlyOnline(): Boolean = onlineState.value
}

internal class FakeNotificationRepository(
    initialNotifications: List<AppNotificationModel> = emptyList()
) : NotificationRepository {
    private val notificationsFlow = MutableStateFlow(initialNotifications)

    override fun observeNotifications(): Flow<List<AppNotificationModel>> = notificationsFlow.asStateFlow()

    override fun observeUnreadNotificationsCount(): Flow<Int> =
        MutableStateFlow(notificationsFlow.value.count { !it.isRead }).asStateFlow()

    override fun observeNotification(notificationId: Long): Flow<AppNotificationModel?> =
        MutableStateFlow(notificationsFlow.value.firstOrNull { it.id == notificationId }).asStateFlow()

    override suspend fun saveNotification(notification: IncomingNotificationModel): Long {
        val nextId = (notificationsFlow.value.maxOfOrNull(AppNotificationModel::id) ?: 0L) + 1L
        notificationsFlow.value = listOf(
            AppNotificationModel(
                id = nextId,
                title = notification.title,
                body = notification.body,
                imageUrl = notification.imageUrl,
                receivedAt = notification.receivedAt,
                isRead = false
            )
        ) + notificationsFlow.value
        return nextId
    }

    override suspend fun markAsRead(notificationId: Long) {
        notificationsFlow.value = notificationsFlow.value.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
    }

    override suspend fun deleteNotification(notificationId: Long) {
        notificationsFlow.value = notificationsFlow.value.filterNot { it.id == notificationId }
    }
}

internal class FakeDangerZoneRepository : DangerZoneRepository {
    val savedZoneIds = MutableStateFlow<List<Int>>(emptyList())
    val savedZones = MutableStateFlow<List<DangerZoneModel>>(emptyList())

    override suspend fun getDangerZonesRemote(bounds: MapBounds): List<DangerZoneModel> = emptyList()

    override suspend fun getDangerZonesForComparisonRemote(): List<DangerZoneComparisonModel> = emptyList()

    override suspend fun saveDangerZoneLocal(zone: DangerZoneModel) {
        savedZoneIds.value = savedZoneIds.value + zone.id
        savedZones.value = savedZones.value + zone
    }

    override suspend fun deleteDangerZoneLocal(id: Int) {
        savedZoneIds.value = savedZoneIds.value.filterNot { it == id }
        savedZones.value = savedZones.value.filterNot { it.id == id }
    }

    override suspend fun getGraphicsStatsRemote(zoneId: Int): StatsGraphicsModel = testStatsGraphicsModel()

    override fun getSavedZoneIds(): Flow<List<Int>> = savedZoneIds.asStateFlow()

    override fun observeSavedDangerZones(): Flow<List<DangerZoneModel>> = savedZones.asStateFlow()
}

internal class FakeGraphicsRepository : GraphicsRepository {
    override suspend fun getGraphicsStats(zoneId: Int): StatsGraphicsModel = testStatsGraphicsModel()
}

internal class FakeLocationSearchRepository : LocationSearchRepository {
    override suspend fun searchCity(query: String): Pair<Double, Double>? = null
}

internal fun testNotification(
    id: Long = 1L,
    title: String = "Alerta de prueba",
    isRead: Boolean = false
) = AppNotificationModel(
    id = id,
    title = title,
    body = "Se ha detectado una incidencia",
    imageUrl = null,
    receivedAt = 1_700_000_000_000,
    isRead = isRead
)

internal fun testDangerZone(
    id: Int = 12,
    zoneName: String = "Lavapies"
) = DangerZoneModel(
    id = id,
    zoneName = zoneName,
    city = "Madrid",
    points = listOf(
        GeoPoint(lat = 40.4, lng = -3.7, order = 2),
        GeoPoint(lat = 40.3, lng = -3.8, order = 1)
    ),
    riskLevel = RiskLevel.MEDIUM,
    povertyRiskRate = 14.2,
    unemploymentRate = 8.1,
    priceSquareMeter = 3_100
)

internal fun testStatsGraphicsModel() = StatsGraphicsModel(
    economy = EconomyStatsModel(
        hoodRent = 900,
        cityRent = 1_200,
        hoodPrice = 3_200,
        cityPrice = 4_100
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
