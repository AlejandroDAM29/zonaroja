package alejandro.developer.zonaroja.ui.screens.main

sealed interface MainUiEvent {
    data class ShowError(val message: String) : MainUiEvent
    data class ShowWarning(val message: String) : MainUiEvent
    data object ShowLogoutSuccessAndNavigateToLogin: MainUiEvent
    data object ShowLogoutError: MainUiEvent
}