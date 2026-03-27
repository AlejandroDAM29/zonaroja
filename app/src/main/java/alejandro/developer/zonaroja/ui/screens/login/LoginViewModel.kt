package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.usecase.LoginWithEmailUseCase
import alejandro.developer.domain.usecase.LoginWithGoogleUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogle: LoginWithGoogleUseCase,
    private val featureFlagsProvider: FeatureFlagsProvider,
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()


    init {
        viewModelScope.launch {
            val flags = runCatching { featureFlagsProvider.get() }
                .getOrDefault(FeatureFlagsModel(googleLoginEnabled = false))
            _uiState.value = LoginUiState(
                isGoogleLoginEnabled = flags.googleLoginEnabled
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun doLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = loginWithEmailUseCase(
                uiState.value.email,
                uiState.value.password
            )

            result.fold(
                onSuccess = {
                    runCatching { syncNotificationSubscriptionsUseCase() }
                    _uiEvents.emit(LoginUiEvent.NavigateToMain)
                },
                onFailure = { exception ->
                    onLoginSessionError(mapLoginErrorToStringRes(exception))
                }
            )

            _uiState.update { it.copy(email = "") }
            _uiState.update { it.copy(password = "") }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onGoogleLoginClicked(): Boolean {
        if (networkMonitor.isCurrentlyOnline()) return true

        viewModelScope.launch {
            _uiEvents.emit(
                LoginUiEvent.ShowErrorGoogleRegister(R.string.error_auth_network)
            )
        }

        return false
    }

    private fun onLoginSessionError(messageRes: Int) {
        viewModelScope.launch {
            _uiEvents.emit(LoginUiEvent.ShowErrorLogin(messageRes))
        }
    }

    fun onGoogleTokenReceived(idToken: String?) {
        if (idToken.isNullOrBlank()) {
            emitGoogleError(
                if (networkMonitor.isCurrentlyOnline()) {
                    R.string.error_auth_generic
                } else {
                    R.string.error_auth_network
                }
            )
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = loginWithGoogle(idToken)

            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    runCatching { syncNotificationSubscriptionsUseCase() }
                    _uiEvents.emit(LoginUiEvent.NavigateToMain)
                },
                onFailure = { throwable ->
                    emitGoogleError(mapLoginErrorToStringRes(throwable))
                }
            )
        }
    }

    private fun emitGoogleError(messageRes: Int) {
        viewModelScope.launch {
            _uiEvents.emit(
                LoginUiEvent.ShowErrorGoogleRegister(messageRes)
            )
        }
    }

    private fun mapLoginErrorToStringRes(throwable: Throwable): Int {
        return when {
            throwable.isNetworkConnectivityError() -> R.string.error_auth_network
            throwable is FirebaseAuthInvalidCredentialsException -> R.string.error_auth_invalid_credentials
            throwable is FirebaseAuthInvalidUserException -> R.string.error_auth_invalid_credentials
            else -> R.string.error_auth_generic
        }
    }
}


