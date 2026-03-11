package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.repositories.LocationSearchRepository
import alejandro.developer.domain.usecase.GetDangerZonesUseCase
import alejandro.developer.domain.usecase.GetGraphicsStatsUseCase
import alejandro.developer.domain.usecase.GetSavedZonesUseCase
import alejandro.developer.domain.usecase.SaveDangerZoneUseCase
import alejandro.developer.domain.usecase.DeleteDangerZoneUseCase
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDangerZonesUseCase: GetDangerZonesUseCase,
    private val locationSearchRepository: LocationSearchRepository,
    private val getGraphicsStatsUseCase: GetGraphicsStatsUseCase,
    private val saveDangerZoneUseCase: SaveDangerZoneUseCase,
    private val getSavedZonesUseCase: GetSavedZonesUseCase,
    private val deleteDangerZoneUseCase: DeleteDangerZoneUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState(isLoading = false))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<MainUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val boundsFlow = MutableSharedFlow<MapBounds>(
        extraBufferCapacity = 1
    )


    init {
        observeBounds()
        observeSavedZones()
    }



    fun observeSavedZones() {
        viewModelScope.launch {

            getSavedZonesUseCase().collect { ids ->

                _uiState.update {
                    it.copy(savedZonesIds = ids)
                }
            }
        }
    }

    fun onFavoriteButtonClicked(zone: DangerZoneModel) {

        viewModelScope.launch {

            if (uiState.value.savedZonesIds.contains(zone.id)) {

                deleteDangerZoneUseCase(zone.id)

            } else {

                saveDangerZoneUseCase(zone)

            }
        }
    }

    fun onMarkerClicked(zone: DangerZoneModel) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedZone = zone) }
            if (uiState.value.savedZonesIds.contains(zone.id)) {
                deleteDangerZoneUseCase(zone.id)
            } else {
                saveDangerZoneUseCase(zone)
            }
            openStats()
        }
    }

    fun openStats() {

        _uiState.update {
            it.copy(
                isPanelOpen = false,
                isStatsOpen = true
            )
        }
        getMapStatsWithZoneId()
    }

    fun openPanel(zone: DangerZoneModel) {
        _uiState.update {
            it.copy(
                isPanelOpen = true,
                isStatsOpen = false,
                selectedZone = zone
            )
        }
    }

    fun closeBottomSheets() {
        _uiState.update {
            it.copy(
                isPanelOpen = false,
                isStatsOpen = false
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    fun clearSearchedLocation() {
        _uiState.update {
            it.copy(searchedLocation = null, searchQuery = "")
        }
    }

    fun searchCity() {
        val query = _uiState.value.searchQuery

        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = locationSearchRepository.searchCity(query)

            result?.let { (lat, lng) ->
                _uiState.update {
                    it.copy(
                        searchedLocation = LatLng(lat, lng),
                        isLoading = false
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun toggleSearch() {
        _uiState.update {
            it.copy(
                isSearchExpanded = !it.isSearchExpanded,
                searchQuery = if (it.isSearchExpanded) "" else it.searchQuery
            )
        }
    }

    private fun getMapStatsWithZoneId() {
        viewModelScope.launch {

            try {

                val zone = _uiState.value.selectedZone ?: return@launch
                _uiState.update { it.copy(isStatsLoading = true) }

                val stats = getGraphicsStatsUseCase(zone.id)

                _uiState.update {
                    it.copy(
                        economyStats = stats.economy,
                        societyStats = stats.society,
                        demographyStats = stats.demography
                    )
                }

            } catch (e: Exception) {
                _uiEvents.emit(MainUiEvent.ShowError(e.message ?: "Unknown error"))
                Log.e("Map Stats Call Error", e.message ?: "Unknown error")

            } finally {
                _uiState.update { it.copy(isStatsLoading = false) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeBounds() {
        viewModelScope.launch {
            boundsFlow
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { bounds ->

                    _uiState.value = _uiState.value.copy(isLoading = true)

                    val dangerPoints = getDangerZonesUseCase(bounds)

                    _uiState.value = _uiState.value.copy(
                        dangerZonesPointModels = dangerPoints,
                        isLoading = false
                    )
                }
        }
    }

    fun onBoundsChanged(bounds: MapBounds) {
        boundsFlow.tryEmit(bounds)
    }

}
