package alejandro.developer.zonaroja.ui.screens.login

import androidx.annotation.StringRes

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isGoogleLoginEnabled: Boolean = false
){
    val canSubmit: Boolean
        get() =
            email.isNotBlank() &&
                    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                    password.length >= 6
}