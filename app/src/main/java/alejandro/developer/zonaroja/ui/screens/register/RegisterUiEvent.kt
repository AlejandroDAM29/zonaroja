package alejandro.developer.zonaroja.ui.screens.register

import androidx.annotation.StringRes

interface RegisterUiEvent {
    data object NavigateToMain : RegisterUiEvent
    data class ShowErrorRegister(
        @StringRes val messageRes: Int
    ) : RegisterUiEvent
}