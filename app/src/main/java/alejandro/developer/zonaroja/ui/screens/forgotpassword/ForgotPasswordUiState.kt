package alejandro.developer.zonaroja.ui.screens.forgotpassword

import android.util.Patterns

data class ForgotPasswordUiState (
    val email: String = "",
    val isLoading: Boolean = false

){
    val canSubmit: Boolean
    get() = email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val showEmailErrorFormat: Boolean
        get() = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && email.isNotBlank()
}