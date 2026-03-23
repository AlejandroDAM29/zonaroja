package alejandro.developer.zonaroja.ui.screens.main

import androidx.annotation.StringRes

sealed interface MainUiEvent {
    data class ShowError(val message: String) : MainUiEvent
    data class ShowErrorRes(
        @StringRes val messageRes: Int
    ) : MainUiEvent
    data class ShowWarning(val message: String) : MainUiEvent
}
