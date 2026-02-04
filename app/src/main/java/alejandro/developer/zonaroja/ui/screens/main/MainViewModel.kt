package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.auth.LogoutUseCase
import alejandro.developer.domain.main.GetCiudadesUseCase
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCiudadesUseCase: GetCiudadesUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(isLoading = true))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<MainUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var texts: List<String> = emptyList()
    private var index = 0

    /*init {
        loadTexts()
    }*/

    private fun loadTexts() {
        viewModelScope.launch {
            texts = getCiudadesUseCase()
            _uiState.value = MainUiState(
                currentText = texts.firstOrNull().orEmpty(),
                isLoading = false
            )
        }
    }

    fun onTextClicked() {
        if (texts.isEmpty()) return
        index = (index + 1) % texts.size

        _uiState.value = _uiState.value.copy(
            currentText = texts[index]
        )
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            loadTexts()
            /*_uiEvents.emit(
                MainUiEvent.ShowWarning(
                    message = "Error al cargar los datos"
                )
            )*/
        }


        /*emitEvent(MainUiEvent.NavigateToLogin)*/
    }

    private fun emitEvent(event: MainUiEvent) {
        viewModelScope.launch {
            _uiEvents.emit(event)
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            try {
                logoutUseCase()
                _uiEvents.emit(MainUiEvent.ShowLogoutSuccessAndNavigateToLogin)

            } catch (e: Exception) {
                Log.e("MainViewModel", "Error during logout", e)
            }
        }
    }

}
