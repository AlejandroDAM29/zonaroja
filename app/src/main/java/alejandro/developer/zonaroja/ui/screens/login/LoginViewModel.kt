package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.models.FeatureFlagsModel
import alejandro.developer.domain.usecase.LoginWithEmailUseCase
import alejandro.developer.domain.usecase.LoginWithGoogleUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.R
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
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
    private val networkMonitor: NetworkMonitor,
    private val appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()


    init {
        viewModelScope.launch {
            val flags = runCatching { featureFlagsProvider.get() }
                .getOrDefault(FeatureFlagsModel(googleLoginEnabled = false))
            _uiState.update {
                it.copy(
                    isGoogleLoginEnabled = !appDataMode.usesModsData && flags.googleLoginEnabled,
                    isValidationBypassed = appDataMode.usesModsData
                )
            }
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
                    onLoginSessionError(mapEmailLoginErrorToStringRes(exception))
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
                    R.string.error_auth_google_response
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
                    onGoogleLoginFailure(throwable)
                }
            )
        }
    }

    fun onGoogleLoginFailure(throwable: Throwable) {
        if (isGoogleLoginCancellation(throwable)) {
            Log.i(TAG, "Google login was cancelled by the user")
            return
        }

        val firebaseErrorCode = (throwable as? FirebaseAuthException)?.errorCode
            ?.let { " code=$it" }
            .orEmpty()

        Log.e(
            TAG,
            "Google login failed$firebaseErrorCode: ${throwable.message ?: "Unknown error"}",
            throwable
        )

        emitGoogleError(mapGoogleLoginErrorToStringRes(throwable))
    }

    private fun emitGoogleError(messageRes: Int) {
        viewModelScope.launch {
            _uiEvents.emit(
                LoginUiEvent.ShowErrorGoogleRegister(messageRes)
            )
        }
    }

    private companion object {
        const val TAG = "LoginViewModel"
    }
}
