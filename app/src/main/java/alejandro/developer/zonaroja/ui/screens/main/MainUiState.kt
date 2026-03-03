package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.models.DangerZone
import com.google.android.gms.maps.model.LatLng

data class MainUiState(
    val currentText: String = "",
    val isLoading: Boolean = false,
    val dangerZonesPoints: List<DangerZone> = emptyList(),
    val searchedLocation: LatLng? = null,
    val searchQuery: String = "",
    val isSearchExpanded: Boolean = false,
    val isPanelOpen: Boolean = false,
    val selectedZone: DangerZone? = null
)
