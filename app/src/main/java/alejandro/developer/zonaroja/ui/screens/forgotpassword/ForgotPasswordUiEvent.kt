package alejandro.developer.zonaroja.ui.screens.forgotpassword

interface ForgotPasswordUiEvent {
    data object ShowErrorResendPassword : ForgotPasswordUiEvent
    data object ShowSuccessResendPassword : ForgotPasswordUiEvent
}