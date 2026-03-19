package alejandro.developer.zonaroja.ui.screens.setting

import alejandro.developer.domain.models.AppCurrency
import alejandro.developer.domain.usecase.DeleteCurrentUserUseCase
import alejandro.developer.domain.usecase.GetCurrentUserEmailUseCase
import alejandro.developer.domain.usecase.IsCurrentUserPasswordProviderUseCase
import alejandro.developer.domain.usecase.LogoutUseCase
import alejandro.developer.domain.usecase.ObserveUserPreferencesUseCase
import alejandro.developer.domain.usecase.ReauthenticateWithEmailUseCase
import alejandro.developer.domain.usecase.SetDarkThemeEnabledUseCase
import alejandro.developer.domain.usecase.SetNotificationsEnabledUseCase
import alejandro.developer.domain.usecase.SetSelectedCurrencyUseCase
import alejandro.developer.zonaroja.R
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase,
    private val isCurrentUserPasswordProviderUseCase: IsCurrentUserPasswordProviderUseCase,
    private val setDarkThemeEnabledUseCase: SetDarkThemeEnabledUseCase,
    private val setSelectedCurrencyUseCase: SetSelectedCurrencyUseCase,
    private val setNotificationsEnabledUseCase: SetNotificationsEnabledUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteCurrentUserUseCase: DeleteCurrentUserUseCase,
    private val reauthenticateWithEmailUseCase: ReauthenticateWithEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<SettingUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            observeUserPreferencesUseCase().collect { preferences ->
                _uiState.update {
                    it.copy(
                        email = getCurrentUserEmailUseCase().orEmpty(),
                        darkThemeEnabled = preferences.darkThemeEnabled,
                        selectedCurrency = preferences.selectedCurrency,
                        notificationsEnabled = preferences.notificationsEnabled,
                        requiresPasswordReauthForDelete = isCurrentUserPasswordProviderUseCase()
                    )
                }
            }
        }
    }

    fun onDarkThemeChanged(enabled: Boolean) {
        viewModelScope.launch {
            setDarkThemeEnabledUseCase(enabled)
        }
    }

    fun onCurrencySelected(currency: AppCurrency) {
        viewModelScope.launch {
            setSelectedCurrencyUseCase(currency)
        }
    }

    fun onNotificationsChanged(enabled: Boolean) {
        viewModelScope.launch {
            setNotificationsEnabledUseCase(enabled)
        }
    }

    fun onNotificationPermissionRevoked() {
        if (!_uiState.value.notificationsEnabled) return

        viewModelScope.launch {
            setNotificationsEnabledUseCase(false)
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching {
                logoutUseCase()
            }.onSuccess {
                _uiEvents.emit(
                    SettingUiEvent.ShowMessage(
                        messageRes = R.string.logout_snackbar_success,
                        type = SettingMessageType.SUCCESS
                    )
                )
                _uiEvents.emit(SettingUiEvent.NavigateToLogin)
            }.onFailure {
                _uiEvents.emit(
                    SettingUiEvent.ShowMessage(
                        messageRes = R.string.logout_snackbar_error,
                        type = SettingMessageType.ERROR
                    )
                )
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDeleteAccountConfirmed(password: String? = null) {
        Log.i("test-100", "El email es: ${_uiState.value.email} y la password es: $password");
        viewModelScope.launch {
            if (_uiState.value.requiresPasswordReauthForDelete && password.isNullOrBlank()) {
                _uiEvents.emit(SettingUiEvent.RequestDeleteAccountReauthentication)
                return@launch
            }

            if (_uiState.value.requiresPasswordReauthForDelete) {
                val resolvedEmail = _uiState.value.email.ifBlank {
                    getCurrentUserEmailUseCase().orEmpty()
                }

                if (resolvedEmail.isBlank()) {
                    _uiEvents.emit(
                        SettingUiEvent.ShowMessage(
                            messageRes = R.string.settings_email_not_available,
                            type = SettingMessageType.ERROR
                        )
                    )
                    return@launch
                }

                _uiState.update { it.copy(email = resolvedEmail) }
            }

            _uiState.update { it.copy(isLoading = true) }

            val reauthResult = if (_uiState.value.requiresPasswordReauthForDelete) {
                val email = _uiState.value.email.ifBlank { getCurrentUserEmailUseCase().orEmpty() }
                reauthenticateWithEmailUseCase(email, password.orEmpty())
            } else {
                Result.success(Unit)
            }

            reauthResult.fold(
                onSuccess = {
                    Log.i("test-100", "Entra en opcion 1");
                    deleteCurrentUserUseCase().fold(
                        onSuccess = {
                            Log.i("test-100", "Entra en opcion 3");
                            runCatching { logoutUseCase() }
                            _uiEvents.emit(
                                SettingUiEvent.ShowMessage(
                                    messageRes = R.string.settings_delete_account_success,
                                    type = SettingMessageType.SUCCESS
                                )
                            )
                            _uiEvents.emit(SettingUiEvent.NavigateToLogin)
                        },
                        onFailure = { throwable ->
                            Log.i("test-100", "Entra en opcion 4");
                            _uiEvents.emit(
                                SettingUiEvent.ShowMessage(
                                    messageRes = mapDeleteAccountError(throwable),
                                    type = SettingMessageType.ERROR
                                )
                            )
                        }
                    )
                },
                onFailure = { throwable ->
                    Log.i("test-100", "Entra en opcion 2");
                    _uiEvents.emit(
                        SettingUiEvent.ShowMessage(
                            messageRes = mapDeleteAccountError(throwable),
                            type = SettingMessageType.ERROR
                        )
                    )
                }
            )

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun mapDeleteAccountError(throwable: Throwable): Int {
        return when (throwable) {
            is FirebaseAuthRecentLoginRequiredException ->
                R.string.settings_delete_account_recent_login_required

            is FirebaseAuthInvalidCredentialsException ->
                R.string.settings_delete_account_invalid_password

            is IllegalStateException ->
                R.string.settings_email_not_available

            is FirebaseNetworkException ->
                R.string.error_auth_network

            else ->
                R.string.settings_delete_account_error
        }
    }
}
