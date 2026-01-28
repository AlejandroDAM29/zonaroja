package alejandro.developer.zonaroja.ui.screens.login

import androidx.annotation.StringRes

interface LoginUiEvent {
    data object NavigateToMain : LoginUiEvent
    data class ShowErrorLogin(
        val message: String,
        val actionLabelText: String,
        val onAction: () -> Unit
    ): LoginUiEvent
    data class ShowErrorRegister(
        @StringRes val messageRes: Int
    ) : LoginUiEvent
    data class ShowSuccessRegister(
        val message: String
    ) : LoginUiEvent
}