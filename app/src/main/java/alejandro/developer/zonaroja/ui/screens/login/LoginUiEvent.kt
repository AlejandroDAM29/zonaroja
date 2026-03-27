package alejandro.developer.zonaroja.ui.screens.login

import androidx.annotation.StringRes

interface LoginUiEvent {
    data object NavigateToMain : LoginUiEvent
    data class ShowErrorLogin(
        @StringRes val messageRes: Int
    ): LoginUiEvent
    data class ShowErrorGoogleRegister(
        @StringRes val messageRes: Int
    ) : LoginUiEvent
}
