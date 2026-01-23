package alejandro.developer.zonaroja.ui.common

sealed interface AppUiEvent {
    data class ShowError(val message: String) : AppUiEvent
}