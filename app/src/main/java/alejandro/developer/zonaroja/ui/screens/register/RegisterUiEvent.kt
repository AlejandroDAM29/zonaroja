package alejandro.developer.zonaroja.ui.screens.register

import alejandro.developer.zonaroja.ui.screens.login.LoginUiEvent
import androidx.annotation.StringRes

interface RegisterUiEvent {
    data object NavigateToMain : RegisterUiEvent
    data class ShowErrorRegister(
        @StringRes val messageRes: Int
    ) : RegisterUiEvent
    data object BackToLogin : RegisterUiEvent
}