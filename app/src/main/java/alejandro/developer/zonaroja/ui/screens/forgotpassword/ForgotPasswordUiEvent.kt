package alejandro.developer.zonaroja.ui.screens.forgotpassword

import androidx.annotation.StringRes

interface ForgotPasswordUiEvent {
    data class ShowErrorResendPassword(
        @StringRes val messageRes: Int
    ) : ForgotPasswordUiEvent
    data object ShowSuccessResendPassword : ForgotPasswordUiEvent
}
