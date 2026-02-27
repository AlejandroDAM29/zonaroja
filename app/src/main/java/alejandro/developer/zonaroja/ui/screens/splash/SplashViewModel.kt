package alejandro.developer.zonaroja.ui.screens.splash

import alejandro.developer.data.providers.FeatureFlagsProvider
import alejandro.developer.domain.usecase.CheckUserSessionUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkUserSession: CheckUserSessionUseCase,
    private val featureFlagsProvider: FeatureFlagsProvider
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<SplashUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            featureFlagsProvider.get()
        }
    }

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
