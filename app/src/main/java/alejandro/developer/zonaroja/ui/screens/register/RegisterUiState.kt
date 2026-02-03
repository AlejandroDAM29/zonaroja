package alejandro.developer.zonaroja.ui.screens.register

import android.util.Patterns

data class RegisterUiState(
    val isLoading: Boolean,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
) {
    val canRegister: Boolean
        get() = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && password.length >= 6
                && password == confirmPassword

    val showEmailErrorFormat: Boolean
        get() = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                && email.isNotBlank()
    val showPasswordsDoNotMatchMessageText: Boolean
        get() = password != confirmPassword
}