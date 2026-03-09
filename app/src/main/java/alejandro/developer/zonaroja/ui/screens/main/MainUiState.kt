package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.models.DangerZone
import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.SocietyStatsModel
import alejandro.developer.domain.models.StatsGraphicsModel
import com.google.android.gms.maps.model.LatLng

data class MainUiState(
    val currentText: String = "",
    val isLoading: Boolean = false,
    val isStatsLoading: Boolean = false,
    val dangerZonesPoints: List<DangerZone> = emptyList(),
    val economyStats: EconomyStatsModel? = null,
    val societyStats: SocietyStatsModel? = null,
    val demographyStats: List<DemographyItemModel> = emptyList(),
    val searchedLocation: LatLng? = null,
    val searchQuery: String = "",
    val isSearchExpanded: Boolean = false,
    val isPanelOpen: Boolean = false,
    val selectedZone: DangerZone? = null,
    val isStatsOpen: Boolean = false
)
