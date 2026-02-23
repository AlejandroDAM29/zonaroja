package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.auth.LogoutUseCase
import alejandro.developer.domain.main.DangerZone
import alejandro.developer.domain.main.GetCiudadesUseCase
import alejandro.developer.domain.main.GetDangerZonesUseCase
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
    private val logoutUseCase: LogoutUseCase,
    private val getDangerZonesUseCase: GetDangerZonesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(isLoading = false))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<MainUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var texts: List<String> = emptyList()
    private var dangerZoneList: List<DangerZone> = emptyList()
    private var index = 0

    init {
        /*loadTexts()*/
        loadDangerZones()
    }

    private fun loadTexts() {
        viewModelScope.launch {
            texts = getCiudadesUseCase()
            _uiState.value = MainUiState(
                currentText = texts.firstOrNull().orEmpty(),
                isLoading = false
            )
        }
    }

    fun loadDangerZones(){
        viewModelScope.launch {
            dangerZoneList = getDangerZonesUseCase()
            _uiState.value = MainUiState(
                currentText = dangerZoneList.firstOrNull()?.points[0]?.lat.toString() ?: "de",
                isLoading = false
            )
        }
    }

    fun onTextClicked() {
        /*if (texts.isEmpty()) return
        index = (index + 1) % texts.size

        _uiState.value = _uiState.value.copy(
            currentText = texts[index]
        )*/

        if (dangerZoneList.isEmpty()) return
        index = (index + 1) % dangerZoneList.size

        _uiState.value = _uiState.value.copy(
            currentText = dangerZoneList[index].zoneName
        )
    }

}
