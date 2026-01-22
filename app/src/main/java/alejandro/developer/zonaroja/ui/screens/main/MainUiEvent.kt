package alejandro.developer.zonaroja.ui.screens.main

sealed interface MainUiEvent {
    data object NavigateToLogin : MainUiEvent
}