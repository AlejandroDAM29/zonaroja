package alejandro.developer.zonaroja.ui.screens.favourites

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val getSavedDangerZonesUseCase: GetSavedDangerZonesUseCase,
    private val deleteDangerZoneUseCase: DeleteDangerZoneUseCase,
    private val getGraphicsStatsUseCase: GetGraphicsStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavouritesUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<FavouritesUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    init {
        observeFavouriteZones()
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
                isStatsLoading = true,
                economyStats = null,
                societyStats = null,
                demographyStats = emptyList()
            )
        }

        viewModelScope.launch {
            try {
                val stats = getGraphicsStatsUseCase(zone.id)
                _uiState.update {
                    it.copy(
                        economyStats = stats.economy,
                        societyStats = stats.society,
                        demographyStats = stats.demography
                    )
                }
            } catch (e: Exception) {
                _uiEvents.emit(
                    FavouritesUiEvent.ShowError(e.message ?: "Unknown error")
                )
                Log.e("Favourites Stats Error", e.message ?: "Unknown error")
            } finally {
                _uiState.update { it.copy(isStatsLoading = false) }
            }
        }
    }

    fun onCloseStats() {
        _uiState.update {
            it.copy(
                isStatsOpen = false,
                selectedZone = null,
                isStatsLoading = false
            )
        }
    }
}
