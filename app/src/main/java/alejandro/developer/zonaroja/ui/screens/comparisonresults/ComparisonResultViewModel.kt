package alejandro.developer.zonaroja.ui.screens.comparisonresults

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.core.runtime.AppDataMode
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ComparisonResultViewModel @Inject constructor(
    private val getDangerZonesComparisonUseCase: GetDangerZonesComparisonUseCase,
    private val networkMonitor: NetworkMonitor,
    private val appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ComparisonResultUiState(
            isLoading = canLoadDangerZoneData(),
            isOffline = !canLoadDangerZoneData()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ComparisonResultUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private var loadedComparisonIds: Pair<Int, Int>? = null
    private var requestedComparisonIds: Pair<Int, Int>? = null

    init {
        if (!appDataMode.usesModsData) {
            observeConnectivity()
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    if (!isOnline) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isOffline = true
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isOffline = false) }
                        requestedComparisonIds?.let { (firstZoneId, secondZoneId) ->
                            loadComparison(
                                firstZoneId = firstZoneId,
                                secondZoneId = secondZoneId,
                                forceRefresh = true
                            )
                        }
                    }
                }
        }
    }

    fun loadComparison(
        firstZoneId: Int,
        secondZoneId: Int,
        forceRefresh: Boolean = false
    ) {
        val currentIds = firstZoneId to secondZoneId
        requestedComparisonIds = currentIds

        if (!forceRefresh && loadedComparisonIds == currentIds && uiState.value.charts != null) {
            return
        }

        if (!canLoadDangerZoneData()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOffline = true
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isOffline = false,
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

                    loadedComparisonIds = currentIds
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOffline = false,
                            firstZone = firstZone,
                            secondZone = secondZone,
                            charts = buildZoneComparisonCharts(firstZone, secondZone),
                            comparisonAvailable = true
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable.isNetworkConnectivityError()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isOffline = true
                            )
                        }
                    } else {
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

    private fun canLoadDangerZoneData(): Boolean {
        return appDataMode.usesModsData || networkMonitor.isCurrentlyOnline()
    }
}
