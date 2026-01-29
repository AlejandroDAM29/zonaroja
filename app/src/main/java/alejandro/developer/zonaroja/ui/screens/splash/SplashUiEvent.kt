package alejandro.developer.zonaroja.ui.screens.splash

sealed interface SplashUiEvent {
    data object NavigateToLogin : SplashUiEvent
    data object NavigateToMain : SplashUiEvent
}