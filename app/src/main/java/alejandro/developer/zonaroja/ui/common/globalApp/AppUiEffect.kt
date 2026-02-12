package alejandro.developer.zonaroja.ui.common.globalApp

sealed interface AppUiEffect {
    data class ShowSnackbarSuccess(val message: String) : AppUiEffect
    data class ShowSnackbarError(val message: String) : AppUiEffect
    data class ShowSnackbarWarning(val message: String) : AppUiEffect

    // Navigation
    data object NavigateToLoginLogout : AppUiEffect
}