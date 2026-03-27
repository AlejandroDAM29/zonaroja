package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.SocietyStatsModel
import com.google.android.gms.maps.model.LatLng

data class MainUiState(
    val currentText: String = "",
    val isLoading: Boolean = false,
    val isMapOffline: Boolean = false,
    val isStatsLoading: Boolean = false,
    val isStatsOffline: Boolean = false,
    val dangerZonesPointModels: List<DangerZoneModel> = emptyList(),
    val economyStats: EconomyStatsModel? = null,
    val societyStats: SocietyStatsModel? = null,
    val demographyStats: List<DemographyItemModel> = emptyList(),
    val searchedLocation: LatLng? = null,
    val searchQuery: String = "",
    val isSearchExpanded: Boolean = false,
    val isPanelOpen: Boolean = false,
    val selectedZone: DangerZoneModel? = null,
    val savedZonesIds: List<Int> = emptyList(),
    val isStatsOpen: Boolean = false
)
