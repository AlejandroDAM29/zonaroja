package alejandro.developer.zonaroja.ui.screens.login

import alejandro.developer.domain.auth.LoginWithEmailUseCase
import alejandro.developer.domain.auth.LoginWithGoogleUseCase
import alejandro.developer.domain.auth.RegisterWithEmailUseCase
import alejandro.developer.zonaroja.R
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
    private val registerWithEmail: RegisterWithEmailUseCase,
    private val loginWithGoogle: LoginWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState(isLoading = false))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<LoginUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()


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

    fun onRegisterClick() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val result = registerWithEmail(
                _uiState.value.email,
                _uiState.value.password
            )

            result.fold(
                onSuccess = {
                    _uiEvents.emit(
                        LoginUiEvent.ShowSuccessRegister(
                            "Registro exitoso"
                        )
                    )
                    _uiEvents.emit(
                        LoginUiEvent.NavigateToMain)
                },
                onFailure = { throwable ->
                    _uiEvents.emit(
                        LoginUiEvent.ShowErrorRegister(
                            mapErrorToStringRes(throwable)
                        )
                    )
                }
            )
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun mapErrorToStringRes(t: Throwable): Int =
        when (t) {
            is FirebaseAuthUserCollisionException ->
                R.string.error_auth_user_exists

            is FirebaseAuthWeakPasswordException ->
                R.string.error_auth_weak_password

            is FirebaseAuthInvalidCredentialsException ->
                R.string.error_auth_invalid_credentials

            is FirebaseNetworkException ->
                R.string.error_auth_network

            else ->
                R.string.error_auth_generic
        }

    fun onGoogleTokenReceived(idToken: String?) {
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

    fun onGoogleError() {
        emitGoogleError()
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


