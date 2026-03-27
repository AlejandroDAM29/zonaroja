package alejandro.developer.zonaroja.ui.screens.setting

import alejandro.developer.core.network.isNetworkConnectivityError
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
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class SettingViewModel @Inject constructor(
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val getCurrentUserEmailUseCase: GetCurrentUserEmailUseCase,
    private val isCurrentUserPasswordProviderUseCase: IsCurrentUserPasswordProviderUseCase,
    private val setDarkThemeEnabledUseCase: SetDarkThemeEnabledUseCase,
    private val setSelectedCurrencyUseCase: SetSelectedCurrencyUseCase,
    private val setNotificationsEnabledUseCase: SetNotificationsEnabledUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase,
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
                if (_uiState.value.isSessionClosing) return@collect

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
            _uiState.update { it.copy(isLoading = true, isSessionClosing = true) }

            runCatching { logoutUseCase() }
                .onSuccess {
                    _uiEvents.emit(
                        SettingUiEvent.NavigateToLoginLogoutSuccess(
                            messageRes = R.string.logout_snackbar_success
                        )
                    )
                    syncNotificationSubscriptionsAfterNavigation()
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(isSessionClosing = false) }
                    _uiEvents.emit(
                        SettingUiEvent.ShowMessage(
                            messageRes = if (throwable.isNetworkConnectivityError()) {
                                R.string.error_auth_network
                            } else {
                                R.string.logout_snackbar_error
                            },
                            type = SettingMessageType.ERROR
                        )
                    )
                }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onDeleteAccountConfirmed(password: String? = null) {
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
                    deleteCurrentUserUseCase().fold(
                        onSuccess = {
                            _uiState.update { it.copy(isSessionClosing = true) }
                            runCatching { logoutUseCase() }
                            _uiEvents.emit(
                                SettingUiEvent.ShowMessage(
                                    messageRes = R.string.settings_delete_account_success,
                                    type = SettingMessageType.SUCCESS
                                )
                            )
                            _uiEvents.emit(SettingUiEvent.NavigateToLogin)
                            syncNotificationSubscriptionsAfterNavigation()
                        },
                        onFailure = { throwable ->
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

    private suspend fun syncNotificationSubscriptionsAfterNavigation() {
        withContext(NonCancellable) {
            runCatching { syncNotificationSubscriptionsUseCase() }
        }
    }

    private fun mapDeleteAccountError(throwable: Throwable): Int {
        return when {
            throwable is FirebaseAuthRecentLoginRequiredException ->
                R.string.settings_delete_account_recent_login_required

            throwable is FirebaseAuthInvalidCredentialsException ->
                R.string.settings_delete_account_invalid_password

            throwable is IllegalStateException ->
                R.string.settings_email_not_available

            throwable.isNetworkConnectivityError() ->
                R.string.error_auth_network

            else ->
                R.string.settings_delete_account_error
        }
    }
}
