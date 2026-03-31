package alejandro.developer.zonaroja.ui.screens.register

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.RegisterWithEmailUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.FakeAuthRepository
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
class RegisterScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun mismatchedPasswords_showValidationMessage() {
        val viewModel = RegisterViewModel(
            registerWithEmail = RegisterWithEmailUseCase(FakeAuthRepository()),
            syncNotificationSubscriptionsUseCase = SyncNotificationSubscriptionsUseCase(
                FakeUserSettingsRepository()
            ),
            appDataMode = AppDataMode.BACKEND
        )

        composeRule.setZonaRojaContent(RecordingAppUiController()) {
            RegisterScreen(
                onBackToLogin = {},
                onNavigateToMain = {},
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.email_register)
        ).performTextInput("user@example.com")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.password_register)
        ).performTextInput("secret1")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.confirm_password_register)
        ).performTextInput("secret2")

        assertTrue(
            composeRule.onAllNodesWithText(
                composeRule.activity.getString(R.string.passwords_not_match)
            ).fetchSemanticsNodes().isNotEmpty()
        )
    }

    @Test
    fun validRegistration_successfullyNavigatesToMain() {
        val authRepository = FakeAuthRepository()
        val viewModel = RegisterViewModel(
            registerWithEmail = RegisterWithEmailUseCase(authRepository),
            syncNotificationSubscriptionsUseCase = SyncNotificationSubscriptionsUseCase(
                FakeUserSettingsRepository()
            ),
            appDataMode = AppDataMode.BACKEND
        )
        var navigatedToMain = false

        composeRule.setZonaRojaContent(RecordingAppUiController()) {
            RegisterScreen(
                onBackToLogin = {},
                onNavigateToMain = { navigatedToMain = true },
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.email_register)
        ).performTextInput("user@example.com")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.password_register)
        ).performTextInput("secret1")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.confirm_password_register)
        ).performTextInput("secret1")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.register_new_user_button)
        ).assertIsEnabled().performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { navigatedToMain }
        assertTrue(navigatedToMain)
    }
}
