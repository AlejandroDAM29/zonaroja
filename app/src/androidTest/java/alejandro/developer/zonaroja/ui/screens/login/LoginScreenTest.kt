package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.usecase.LoginWithEmailUseCase
import alejandro.developer.domain.usecase.LoginWithGoogleUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.FakeAuthRepository
import alejandro.developer.zonaroja.FakeFeatureFlagsRepository
import alejandro.developer.zonaroja.FakeNetworkMonitor
import alejandro.developer.zonaroja.FakeUserSettingsRepository
import alejandro.developer.zonaroja.RecordingAppUiController
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.setZonaRojaContent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun emailLogin_successfullyNavigatesToMain() {
        val controller = RecordingAppUiController()
        val authRepository = FakeAuthRepository()
        val viewModel = LoginViewModel(
            loginWithEmailUseCase = LoginWithEmailUseCase(authRepository),
            loginWithGoogle = LoginWithGoogleUseCase(authRepository),
            featureFlagsProvider = FeatureFlagsProvider(
                FakeFeatureFlagsRepository(FeatureFlagsModel(googleLoginEnabled = false))
            ),
            syncNotificationSubscriptionsUseCase = SyncNotificationSubscriptionsUseCase(
                FakeUserSettingsRepository()
            ),
            networkMonitor = FakeNetworkMonitor(initialOnline = true),
            appDataMode = AppDataMode.BACKEND
        )
        var navigatedToMain = false

        composeRule.setZonaRojaContent(controller) {
            LoginScreen(
                navigateToMain = { navigatedToMain = true },
                viewModel = viewModel,
                navigateToRegister = {},
                navigateToForgotPassword = {}
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.mail_placeholder)
        ).performTextInput("user@example.com")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.password_placeholder)
        ).performTextInput("secret1")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.init_session_button)
        ).assertIsEnabled().performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { navigatedToMain }
        assertTrue(navigatedToMain)
    }

    @Test
    fun googleLoginButton_isVisibleWhenFeatureFlagIsEnabled() {
        val authRepository = FakeAuthRepository()
        val viewModel = LoginViewModel(
            loginWithEmailUseCase = LoginWithEmailUseCase(authRepository),
            loginWithGoogle = LoginWithGoogleUseCase(authRepository),
            featureFlagsProvider = FeatureFlagsProvider(
                FakeFeatureFlagsRepository(FeatureFlagsModel(googleLoginEnabled = true))
            ),
            syncNotificationSubscriptionsUseCase = SyncNotificationSubscriptionsUseCase(
                FakeUserSettingsRepository()
            ),
            networkMonitor = FakeNetworkMonitor(initialOnline = true),
            appDataMode = AppDataMode.BACKEND
        )

        composeRule.setZonaRojaContent {
            LoginScreen(
                navigateToMain = {},
                viewModel = viewModel,
                navigateToRegister = {},
                navigateToForgotPassword = {}
            )
        }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.login_with_google)
            ).fetchSemanticsNodes().isNotEmpty()
        }

        assertTrue(
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.login_with_google)
            ).fetchSemanticsNodes().isNotEmpty()
        )
    }
}
