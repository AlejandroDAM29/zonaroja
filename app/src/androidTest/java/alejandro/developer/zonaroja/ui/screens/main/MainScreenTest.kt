package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.DeleteDangerZoneUseCase
import alejandro.developer.domain.usecase.GetDangerZonesUseCase
import alejandro.developer.domain.usecase.GetGraphicsStatsUseCase
import alejandro.developer.domain.usecase.GetSavedZonesUseCase
import alejandro.developer.domain.usecase.SaveDangerZoneUseCase
import alejandro.developer.zonaroja.FakeDangerZoneRepository
import alejandro.developer.zonaroja.FakeGraphicsRepository
import alejandro.developer.zonaroja.FakeLocationSearchRepository
import alejandro.developer.zonaroja.FakeNetworkMonitor
import alejandro.developer.zonaroja.RecordingAppUiController
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.setZonaRojaContent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun offlineState_showsNoInternetMessage() {
        val viewModel = buildViewModel(isOnline = false)

        composeRule.setZonaRojaContent(RecordingAppUiController()) {
            MainScreen(
                showSnackbarRegisterSuccess = false,
                onRegisterSuccessSnackbarShown = {},
                viewModel = viewModel
            )
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.no_internet_title)
            ).fetchSemanticsNodes().isNotEmpty()
        }

        assertTrue(
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.no_internet_title)
            ).fetchSemanticsNodes().isNotEmpty()
        )
        assertTrue(
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.no_internet_map_description)
            ).fetchSemanticsNodes().isNotEmpty()
        )
    }

    @Test
    fun registerSuccessFlag_showsSnackbarAndConsumesCallback() {
        val controller = RecordingAppUiController()
        val viewModel = buildViewModel(isOnline = false)
        var snackbarConsumed = false

        composeRule.setZonaRojaContent(controller) {
            MainScreen(
                showSnackbarRegisterSuccess = true,
                onRegisterSuccessSnackbarShown = { snackbarConsumed = true },
                viewModel = viewModel
            )
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            controller.successMessages.isNotEmpty() && snackbarConsumed
        }

        assertTrue(snackbarConsumed)
        assertEquals(
            composeRule.activity.getString(R.string.register_success_snackbar),
            controller.successMessages.last()
        )
    }

    private fun buildViewModel(isOnline: Boolean): MainViewModel {
        val dangerZoneRepository = FakeDangerZoneRepository()
        return MainViewModel(
            getDangerZonesUseCase = GetDangerZonesUseCase(dangerZoneRepository),
            locationSearchRepository = FakeLocationSearchRepository(),
            getGraphicsStatsUseCase = GetGraphicsStatsUseCase(FakeGraphicsRepository()),
            saveDangerZoneUseCase = SaveDangerZoneUseCase(dangerZoneRepository),
            getSavedZonesUseCase = GetSavedZonesUseCase(dangerZoneRepository),
            deleteDangerZoneUseCase = DeleteDangerZoneUseCase(dangerZoneRepository),
            networkMonitor = FakeNetworkMonitor(initialOnline = isOnline),
            appDataMode = AppDataMode.BACKEND
        )
    }
}
