package alejandro.developer.zonaroja.ui.screens.favourites

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.usecase.DeleteDangerZoneUseCase
import alejandro.developer.domain.usecase.GetGraphicsStatsUseCase
import alejandro.developer.domain.usecase.GetSavedDangerZonesUseCase
import android.util.Log
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
class FavouritesViewModel @Inject constructor(
    private val getSavedDangerZonesUseCase: GetSavedDangerZonesUseCase,
    private val deleteDangerZoneUseCase: DeleteDangerZoneUseCase,
    private val getGraphicsStatsUseCase: GetGraphicsStatsUseCase,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<FavouritesUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        observeFavouriteZones()
        observeConnectivity()
    }

    private fun observeFavouriteZones() {
        viewModelScope.launch {
            getSavedDangerZonesUseCase().collect { zones ->
                _uiState.update { currentState ->
                    val selectedZone = currentState.selectedZone
                    val selectedZoneStillExists = zones.any { it.id == selectedZone?.id }
                    val expandedZoneStillExists = zones.any { it.id == currentState.expandedZoneId }

                    currentState.copy(
                        isLoading = false,
                        favouriteZones = zones,
                        expandedZoneId = currentState.expandedZoneId.takeIf { expandedZoneStillExists },
                        selectedZone = selectedZone?.let { zone ->
                            zones.firstOrNull { it.id == zone.id }
                        },
                        isStatsOpen = currentState.isStatsOpen && selectedZoneStillExists
                    )
                }
            }
        }
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    if (!_uiState.value.isStatsOpen) return@collect

                    if (!isOnline) {
                        _uiState.update {
                            it.copy(
                                isStatsLoading = false,
                                isStatsOffline = true,
                                economyStats = null,
                                societyStats = null,
                                demographyStats = emptyList()
                            )
                        }
                    } else if (_uiState.value.isStatsOffline) {
                        _uiState.value.selectedZone?.let(::loadStats)
                    }
                }
        }
    }

    fun onZoneClicked(zoneId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                expandedZoneId = if (currentState.expandedZoneId == zoneId) null else zoneId
            )
        }
    }

    fun onDeleteClicked(zoneId: Int) {
        viewModelScope.launch {
            deleteDangerZoneUseCase(zoneId)
        }
    }

    fun onOpenStats(zone: DangerZoneModel) {
        _uiState.update {
            it.copy(
                selectedZone = zone,
                isStatsOpen = true,
                isStatsLoading = false,
                isStatsOffline = false,
                economyStats = null,
                societyStats = null,
                demographyStats = emptyList()
            )
        }

        loadStats(zone)
    }

    private fun loadStats(zone: DangerZoneModel) {
        viewModelScope.launch {
            if (!networkMonitor.isCurrentlyOnline()) {
                _uiState.update {
                    it.copy(
                        isStatsLoading = false,
                        isStatsOffline = true
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isStatsLoading = true,
                    isStatsOffline = false
                )
            }

            runCatching { getGraphicsStatsUseCase(zone.id) }
                .onSuccess { stats ->
                    _uiState.update {
                        it.copy(
                            economyStats = stats.economy,
                            societyStats = stats.society,
                            demographyStats = stats.demography,
                            isStatsOffline = false
                        )
                    }
                }
                .onFailure { throwable ->
                    if (throwable.isNetworkConnectivityError()) {
                        _uiState.update {
                            it.copy(
                                isStatsOffline = true,
                                economyStats = null,
                                societyStats = null,
                                demographyStats = emptyList()
                            )
                        }
                    } else {
                        _uiEvents.emit(
                            FavouritesUiEvent.ShowError(throwable.message ?: "Unknown error")
                        )
                        Log.e("Favourites Stats Error", throwable.message ?: "Unknown error")
                    }
                }

            _uiState.update { it.copy(isStatsLoading = false) }
        }
    }

    fun onCloseStats() {
        _uiState.update {
            it.copy(
                isStatsOpen = false,
                selectedZone = null,
                isStatsLoading = false,
                isStatsOffline = false,
                economyStats = null,
                societyStats = null,
                demographyStats = emptyList()
            )
        }
    }
}
