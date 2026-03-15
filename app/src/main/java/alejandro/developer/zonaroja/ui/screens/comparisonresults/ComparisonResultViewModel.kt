package alejandro.developer.zonaroja.ui.screens.comparisonresults

import alejandro.developer.domain.usecase.GetDangerZonesComparisonUseCase
import alejandro.developer.zonaroja.ui.mappers.buildZoneComparisonCharts
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
class ComparisonResultViewModel @Inject constructor(
    private val getDangerZonesComparisonUseCase: GetDangerZonesComparisonUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparisonResultUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ComparisonResultUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var loadedComparisonIds: Pair<Int, Int>? = null

    fun loadComparison(firstZoneId: Int, secondZoneId: Int) {
        val currentIds = firstZoneId to secondZoneId
        if (loadedComparisonIds == currentIds) return

        loadedComparisonIds = currentIds

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    firstZone = null,
                    secondZone = null,
                    charts = null,
                    comparisonAvailable = true
                )
            }

            runCatching { getDangerZonesComparisonUseCase() }
                .onSuccess { zones ->
                    val firstZone = zones.firstOrNull { it.id == firstZoneId }
                    val secondZone = zones.firstOrNull { it.id == secondZoneId }

                    if (firstZone == null || secondZone == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                comparisonAvailable = false
                            )
                        }
                        _uiEvents.emit(
                            ComparisonResultUiEvent.ShowError(
                                "No se ha podido construir la comparativa con las zonas seleccionadas."
                            )
                        )
                        return@onSuccess
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            firstZone = firstZone,
                            secondZone = secondZone,
                            charts = buildZoneComparisonCharts(firstZone, secondZone),
                            comparisonAvailable = true
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            comparisonAvailable = false
                        )
                    }
                    _uiEvents.emit(
                        ComparisonResultUiEvent.ShowError(
                            throwable.message ?: "No se ha podido cargar la comparativa."
                        )
                    )
                }
        }
    }
}
