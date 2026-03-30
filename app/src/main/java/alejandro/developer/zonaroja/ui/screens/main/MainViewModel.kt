package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.core.network.NetworkMonitor
import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.MapBounds
import alejandro.developer.domain.repositories.LocationSearchRepository
import alejandro.developer.domain.usecase.DeleteDangerZoneUseCase
import alejandro.developer.domain.usecase.GetDangerZonesUseCase
import alejandro.developer.domain.usecase.GetGraphicsStatsUseCase
import alejandro.developer.domain.usecase.GetSavedZonesUseCase
import alejandro.developer.domain.usecase.SaveDangerZoneUseCase
import alejandro.developer.zonaroja.R
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDangerZonesUseCase: GetDangerZonesUseCase,
    private val locationSearchRepository: LocationSearchRepository,
    private val getGraphicsStatsUseCase: GetGraphicsStatsUseCase,
    private val saveDangerZoneUseCase: SaveDangerZoneUseCase,
    private val getSavedZonesUseCase: GetSavedZonesUseCase,
    private val deleteDangerZoneUseCase: DeleteDangerZoneUseCase,
    private val networkMonitor: NetworkMonitor,
    private val appDataMode: AppDataMode
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            isLoading = false,
            isMapOffline = !canLoadDangerZoneData()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvents = MutableSharedFlow<MainUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    private val boundsFlow = MutableSharedFlow<MapBounds>(extraBufferCapacity = 1)

    init {
        observeConnectivity()
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
                isStatsOpen = true,
                isStatsOffline = false
            )
        }
        getMapStatsWithZoneId()
    }

    fun openPanel(zone: DangerZoneModel) {
        _uiState.update {
            it.copy(
                isPanelOpen = true,
                isStatsOpen = false,
                isStatsOffline = false,
                selectedZone = zone
            )
        }
    }

    fun closeBottomSheets() {
        _uiState.update {
            it.copy(
                isPanelOpen = false,
                isStatsOpen = false,
                isStatsLoading = false,
                isStatsOffline = false
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

        if (!networkMonitor.isCurrentlyOnline()) {
            viewModelScope.launch {
                _uiEvents.emit(MainUiEvent.ShowErrorRes(R.string.error_auth_network))
            }
            return
        }

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

    private fun observeConnectivity() {
        if (appDataMode.usesModsData) {
            _uiState.update { it.copy(isMapOffline = false) }
            return
        }

        viewModelScope.launch {
            networkMonitor.isOnline
                .distinctUntilChanged()
                .collect { isOnline ->
                    if (!isOnline) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                isMapOffline = true,
                                isLoading = false,
                                isStatsLoading = false,
                                isStatsOffline = currentState.isStatsOpen
                            )
                        }
                    } else {
                        val shouldReloadStats = uiState.value.isStatsOpen &&
                            uiState.value.isStatsOffline &&
                            uiState.value.selectedZone != null

                        _uiState.update {
                            it.copy(isMapOffline = false)
                        }

                        if (shouldReloadStats) {
                            getMapStatsWithZoneId()
                        }
                    }
                }
        }
    }

    private fun getMapStatsWithZoneId() {
        viewModelScope.launch {
            val zone = _uiState.value.selectedZone ?: return@launch

            if (!canLoadDangerZoneData()) {
                _uiState.update {
                    it.copy(
                        isStatsLoading = false,
                        isStatsOffline = true,
                        economyStats = null,
                        societyStats = null,
                        demographyStats = emptyList()
                    )
                }
                return@launch
            }

            try {
                _uiState.update {
                    it.copy(
                        isStatsLoading = true,
                        isStatsOffline = false
                    )
                }

                val stats = getGraphicsStatsUseCase(zone.id)

                _uiState.update {
                    it.copy(
                        economyStats = stats.economy,
                        societyStats = stats.society,
                        demographyStats = stats.demography,
                        isStatsOffline = false
                    )
                }
            } catch (throwable: Exception) {
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
                    _uiEvents.emit(MainUiEvent.ShowWarning(throwable.message ?: "Unknown error"))
                    Log.e("Map Stats Call Error", throwable.message ?: "Unknown error")
                }
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
                    if (!canLoadDangerZoneData()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isMapOffline = true,
                                dangerZonesPointModels = emptyList()
                            )
                        }
                        return@collectLatest
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            isMapOffline = false
                        )
                    }

                    runCatching { getDangerZonesUseCase(bounds) }
                        .onSuccess { dangerPoints ->
                            _uiState.update {
                                it.copy(
                                    dangerZonesPointModels = dangerPoints,
                                    isLoading = false,
                                    isMapOffline = false
                                )
                            }
                        }
                        .onFailure { throwable ->
                            if (throwable.isNetworkConnectivityError()) {
                                _uiState.update {
                                    it.copy(
                                        dangerZonesPointModels = emptyList(),
                                        isLoading = false,
                                        isMapOffline = true
                                    )
                                }
                            } else {
                                _uiState.update { it.copy(isLoading = false) }
                                _uiEvents.emit(
                                    MainUiEvent.ShowWarning(
                                        throwable.message ?: "Unknown error"
                                    )
                                )
                            }
                        }
                }
        }
    }

    fun onBoundsChanged(bounds: MapBounds) {
        boundsFlow.tryEmit(bounds)
    }

    private fun canLoadDangerZoneData(): Boolean {
        return appDataMode.usesModsData || networkMonitor.isCurrentlyOnline()
    }
}
