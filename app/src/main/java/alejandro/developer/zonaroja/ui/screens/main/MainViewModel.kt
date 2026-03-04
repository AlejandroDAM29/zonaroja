package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.usecase.LogoutUseCase
import alejandro.developer.domain.usecase.GetCiudadesUseCase
import alejandro.developer.domain.usecase.GetDangerZonesUseCase
import alejandro.developer.domain.repositories.LocationSearchRepository
import alejandro.developer.domain.models.MapBounds
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val locationSearchRepository: LocationSearchRepository
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
    }

    fun openStats() {
        _uiState.update {
            it.copy(
                isPanelOpen = false,
                isStatsOpen = true
            )
        }
    }

    fun openPanel(zone: DangerZone) {
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
                        dangerZonesPoints = dangerPoints,
                        isLoading = false
                    )
                }
        }
    }

    fun onBoundsChanged(bounds: MapBounds) {
        boundsFlow.tryEmit(bounds)
    }

}
