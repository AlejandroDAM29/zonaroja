package alejandro.developer.zonaroja.ui.screens.login

data class LoginUiState (
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isGoogleLoginEnabled: Boolean = false,
    val isValidationBypassed: Boolean = false
){
    val canSubmit: Boolean
        get() = if (isValidationBypassed) {
            true
        } else {
            email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                password.length >= 6
        }
}
