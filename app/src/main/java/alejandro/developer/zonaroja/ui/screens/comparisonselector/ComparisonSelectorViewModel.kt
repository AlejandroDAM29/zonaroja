package alejandro.developer.zonaroja.ui.screens.comparisonselector

import alejandro.developer.domain.usecase.GetDangerZonesComparisonUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ComparisonSelectorViewModel @Inject constructor(
    private val getDangerZonesComparisonUseCase: GetDangerZonesComparisonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonSelectorUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ComparisonSelectorUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        loadZones()
    }

    private fun loadZones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            runCatching { getDangerZonesComparisonUseCase() }
                .onSuccess { zones ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableZones = zones.sortedWith(
                                compareBy({ zone -> zone.zoneName.lowercase() }, { zone -> zone.city.lowercase() })
                            )
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            availableZones = emptyList()
                        )
                    }
                    _uiEvents.emit(
                        ComparisonSelectorUiEvent.ShowError(
                            throwable.message ?: "No se han podido cargar las zonas."
                        )
                    )
                }
        }
    }

    fun onFirstZoneSelected(zoneId: Int?) {
        _uiState.update { currentState ->
            currentState.copy(
                firstSelectedZoneId = zoneId,
                secondSelectedZoneId = currentState.secondSelectedZoneId.takeUnless { it == zoneId }
            )
        }
    }

    fun onSecondZoneSelected(zoneId: Int?) {
        _uiState.update { currentState ->
            currentState.copy(
                secondSelectedZoneId = zoneId,
                firstSelectedZoneId = currentState.firstSelectedZoneId.takeUnless { it == zoneId }
            )
        }
    }

    fun onCompareClicked() {
        val currentState = uiState.value
        val firstZoneId = currentState.firstSelectedZoneId ?: return
        val secondZoneId = currentState.secondSelectedZoneId ?: return

        viewModelScope.launch {
            _uiEvents.emit(
                ComparisonSelectorUiEvent.NavigateToResult(
                    firstZoneId = firstZoneId,
                    secondZoneId = secondZoneId
                )
            )
        }
    }
}
