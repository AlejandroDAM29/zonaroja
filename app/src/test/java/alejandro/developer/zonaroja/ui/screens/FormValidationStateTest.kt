package alejandro.developer.zonaroja.ui.screens

import alejandro.developer.zonaroja.ui.screens.forgotpassword.ForgotPasswordUiState
import alejandro.developer.zonaroja.ui.screens.login.LoginUiState
import alejandro.developer.zonaroja.ui.screens.register.RegisterUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FormValidationStateTest {

    @Test
    fun loginUiState_canSubmitOnlyWithValidEmailAndPassword() {
        assertFalse(LoginUiState(email = "bad", password = "123").canSubmit)
        assertTrue(LoginUiState(email = "user@example.com", password = "123456").canSubmit)
        assertTrue(
            LoginUiState(
                email = "",
                password = "",
                isValidationBypassed = true
            ).canSubmit
        )
    }

    @Test
    fun forgotPasswordUiState_exposesSubmitAndErrorFlags() {
        assertFalse(ForgotPasswordUiState(email = "").canSubmit)
        assertFalse(ForgotPasswordUiState(email = "").showEmailErrorFormat)
        assertTrue(ForgotPasswordUiState(email = "bad-email").showEmailErrorFormat)
        assertTrue(ForgotPasswordUiState(email = "user@example.com").canSubmit)
    }

    @Test
    fun registerUiState_respectsValidationAndBypassFlags() {
        val invalid = RegisterUiState(
            isLoading = false,
            email = "bad",
            password = "123456",
            confirmPassword = "123"
        )
        val valid = RegisterUiState(
            isLoading = false,
            email = "user@example.com",
            password = "123456",
            confirmPassword = "123456"
        )
        val bypassed = RegisterUiState(
            isLoading = false,
            isValidationBypassed = true
        )

        assertFalse(invalid.canRegister)
        assertTrue(invalid.showEmailErrorFormat)
        assertTrue(invalid.showPasswordsDoNotMatchMessageText)
        assertTrue(valid.canRegister)
        assertTrue(bypassed.canRegister)
    }
}
