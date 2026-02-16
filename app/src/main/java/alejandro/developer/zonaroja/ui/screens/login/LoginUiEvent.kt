package alejandro.developer.zonaroja.ui.screens.login

import androidx.annotation.StringRes

interface LoginUiEvent {
    data object NavigateToMain : LoginUiEvent
    data class ShowErrorLogin(
        val message: String
    ): LoginUiEvent
    data class ShowErrorGoogleRegister(
        @StringRes val messageRes: Int
    ) : LoginUiEvent
}