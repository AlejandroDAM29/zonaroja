package alejandro.developer.zonaroja.ui.screens.register

import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.RegisterWithEmailUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmail: RegisterWithEmailUseCase,
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase,
    appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RegisterUiState(
            isLoading = false,
            isValidationBypassed = appDataMode.usesModsData
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<RegisterUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value) }


    fun onPasswordChange(value: String) =
        _uiState.update { it.copy(password = value) }


    fun onConfirmPasswordChange(value: String) =
        _uiState.update { it.copy(confirmPassword = value) }

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
                    runCatching { syncNotificationSubscriptionsUseCase() }
                    _uiEvents.emit(
                        RegisterUiEvent.NavigateToMain)
                },
                onFailure = { throwable ->
                    _uiEvents.emit(
                        RegisterUiEvent.ShowErrorRegister(
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

            /*t.isNetworkConnectivityError() ->
                R.string.error_auth_network*/

            else ->
                R.string.error_auth_generic
        }
}
