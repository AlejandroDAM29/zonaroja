package alejandro.developer.zonaroja.ui.screens.changepassword

import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.GetCurrentUserEmailUseCase
import alejandro.developer.domain.usecase.SendPasswordResetEmailUseCase
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.screens.setting.SettingMessageType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase,
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase,
    private val appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChangePasswordUiState(
            email = getCurrentUserEmailUseCase().orEmpty()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ChangePasswordUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onSendResetEmailClicked() {
        val email = _uiState.value.email
        if (email.isBlank()) {
            viewModelScope.launch {
                _uiEvents.emit(
                    ChangePasswordUiEvent.ShowMessage(
                        messageRes = R.string.settings_email_not_available,
                        type = SettingMessageType.ERROR
                    )
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            sendPasswordResetEmailUseCase(email).fold(
                onSuccess = {
                    _uiEvents.emit(
                        ChangePasswordUiEvent.ShowMessage(
                            messageRes = if (appDataMode.usesModsData) {
                                R.string.mods_password_reset_disabled_message
                            } else {
                                R.string.resend_password_success_message
                            },
                            type = SettingMessageType.SUCCESS
                        )
                    )
                },
                onFailure = { throwable ->
                    _uiEvents.emit(
                        ChangePasswordUiEvent.ShowMessage(
                            messageRes = if (throwable.isNetworkConnectivityError()) {
                                R.string.error_auth_network
                            } else {
                                R.string.resend_email_error_message
                            },
                            type = SettingMessageType.ERROR
                        )
                    )
                }
            )

            _uiState.update { it.copy(isSending = false) }
        }
    }
}
