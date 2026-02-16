package alejandro.developer.zonaroja.ui.common.globalApp

import alejandro.developer.domain.auth.LogoutUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<AppUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onLogoutClicked() {
        viewModelScope.launch {
            try {
                logoutUseCase()

                _uiEffect.emit(AppUiEffect.NavigateToLoginLogoutSuccess(
                    message = "Logout correcto"
                )
                )

            } catch (e: Exception) {

                _uiEffect.emit(
                    AppUiEffect.ShowSnackbarError("Error al cerrar sesión")
                )
            }
        }
    }
}
