package alejandro.developer.zonaroja.ui.screens.comparisonselector

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.usecase.GetDangerZonesComparisonUseCase
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
class ComparisonSelectorViewModel @Inject constructor(
    private val getDangerZonesComparisonUseCase: GetDangerZonesComparisonUseCase,
    private val networkMonitor: NetworkMonitor,
    private val appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ComparisonSelectorUiState(
            isLoading = canLoadDangerZoneData(),
            isOffline = !canLoadDangerZoneData()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<ComparisonSelectorUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        if (appDataMode.usesModsData) {
            loadZones()
        } else {
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
                                isOffline = true,
                                availableZones = emptyList()
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isOffline = false) }
                        loadZones()
                    }
                }
        }
    }

    private fun loadZones() {
        if (!canLoadDangerZoneData()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isOffline = true,
                    availableZones = emptyList()
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isOffline = false
                )
            }

            runCatching { getDangerZonesComparisonUseCase() }
                .onSuccess { zones ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isOffline = false,
                            availableZones = zones.sortedWith(
                                compareBy(
                                    { zone -> zone.zoneName.lowercase() },
                                    { zone -> zone.city.lowercase() }
                                )
                            )
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable.isNetworkConnectivityError()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isOffline = true,
                                availableZones = emptyList()
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isOffline = false,
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

    private fun canLoadDangerZoneData(): Boolean {
        return appDataMode.usesModsData || networkMonitor.isCurrentlyOnline()
    }
}
