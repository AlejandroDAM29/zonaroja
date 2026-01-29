package alejandro.developer.zonaroja.ui.screens.splash

import alejandro.developer.domain.auth.CheckUserSessionUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkUserSession: CheckUserSessionUseCase
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<SplashUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onSplashShown() {
        viewModelScope.launch {
            val event = if (checkUserSession()) {
                SplashUiEvent.NavigateToMain
            } else {
                SplashUiEvent.NavigateToLogin
            }
            _uiEvent.emit(event)
        }
    }

}
