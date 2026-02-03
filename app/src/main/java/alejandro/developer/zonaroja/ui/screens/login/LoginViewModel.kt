package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.auth.LoginWithEmailUseCase
import alejandro.developer.domain.auth.LoginWithGoogleUseCase
import alejandro.developer.domain.auth.RegisterWithEmailUseCase
import alejandro.developer.domain.common.FeatureFlagsRepository
import alejandro.developer.zonaroja.R
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
    private val featureFlagsProvider: FeatureFlagsProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(isLoading = false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()


    init {
        viewModelScope.launch {
            val flags = featureFlagsProvider.get()
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
                    _uiEvents.emit(LoginUiEvent.NavigateToMain)
                },
                onFailure = { exception ->
                    onLoginSessionError(exception)
                }
            )

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onLoginSessionError(exception: Throwable) {
        viewModelScope.launch {
            _uiEvents.emit(
                LoginUiEvent.ShowErrorLogin(
                    exception.message.toString(),
                    "Cerrar",
                    {})
            )
        }
    }

    fun onGoogleTokenReceived(idToken: String?) {
        Log.i("test-100", "El token es: $idToken");
        if (idToken.isNullOrBlank()) {
            emitGoogleError()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = loginWithGoogle(idToken)

            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = {
                    _uiEvents.emit(LoginUiEvent.NavigateToMain)
                },
                onFailure = {
                    emitGoogleError()
                }
            )
        }
    }

    private fun emitGoogleError() {
        viewModelScope.launch {
            _uiEvents.emit(
                LoginUiEvent.ShowErrorRegister(
                    R.string.error_auth_generic
                )
            )
        }
    }
}


