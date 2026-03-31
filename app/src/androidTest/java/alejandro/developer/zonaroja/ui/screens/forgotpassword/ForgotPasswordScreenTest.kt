package alejandro.developer.zonaroja.ui.screens.forgotpassword

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.SendPasswordResetEmailUseCase
import alejandro.developer.zonaroja.FakeAuthRepository
import alejandro.developer.zonaroja.RecordingAppUiController
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.setZonaRojaContent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ForgotPasswordScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun sendResetEmail_showsSuccessSnackbar() {
        val controller = RecordingAppUiController()
        val viewModel = ForgotPasswordViewModel(
            sendPasswordResetEmailUseCase = SendPasswordResetEmailUseCase(FakeAuthRepository()),
            appDataMode = AppDataMode.BACKEND
        )

        composeRule.setZonaRojaContent(controller) {
            ForgotPasswordScreen(
                onBackToLogin = {},
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.email_forgot_password)
        ).performTextInput("user@example.com")
        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.register_new_user_button)
        ).assertIsEnabled().performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { controller.successMessages.isNotEmpty() }
        assertEquals(
            composeRule.activity.getString(R.string.resend_password_success_message),
            controller.successMessages.last()
        )
    }

    @Test
    fun backLink_navigatesToLogin() {
        val viewModel = ForgotPasswordViewModel(
            sendPasswordResetEmailUseCase = SendPasswordResetEmailUseCase(FakeAuthRepository()),
            appDataMode = AppDataMode.BACKEND
        )
        var navigatedBack = false

        composeRule.setZonaRojaContent(RecordingAppUiController()) {
            ForgotPasswordScreen(
                onBackToLogin = { navigatedBack = true },
                viewModel = viewModel
            )
        }

        composeRule.onNodeWithText(
            composeRule.activity.getString(R.string.comeback_login_from_resend_password)
        ).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) { navigatedBack }
        assertTrue(navigatedBack)
    }
}
