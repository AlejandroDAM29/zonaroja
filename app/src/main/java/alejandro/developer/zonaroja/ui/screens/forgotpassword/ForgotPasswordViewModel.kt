package alejandro.developer.zonaroja.ui.screens.forgotpassword

import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.SendPasswordResetEmailUseCase
import alejandro.developer.zonaroja.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ForgotPasswordUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            sendPasswordResetEmailUseCase(email).fold(
                onSuccess = {
                    _uiEvents.emit(
                        ForgotPasswordUiEvent.ShowSuccessResendPassword(
                            messageRes = if (appDataMode.usesModsData) {
                                R.string.mods_password_reset_disabled_message
                            } else {
                                R.string.resend_password_success_message
                            }
                        )
                    )
                },
                onFailure = { throwable ->
                    _uiEvents.emit(
                        ForgotPasswordUiEvent.ShowErrorResendPassword(
                            messageRes = if (throwable.isNetworkConnectivityError()) {
                                R.string.error_auth_network
                            } else {
                                R.string.resend_email_error_message
                            }
                        )
                    )
                }
            )

            _uiState.update { it.copy(email = "") }

            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
