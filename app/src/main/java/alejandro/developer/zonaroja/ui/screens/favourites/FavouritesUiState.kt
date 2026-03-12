package alejandro.developer.zonaroja.ui.screens.favourites

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.DemographyItemModel
import alejandro.developer.domain.models.EconomyStatsModel
import alejandro.developer.domain.models.SocietyStatsModel

data class FavouritesUiState(
    val isLoading: Boolean = true,
    val favouriteZones: List<DangerZoneModel> = emptyList(),
    val expandedZoneId: Int? = null,
    val selectedZone: DangerZoneModel? = null,
    val isStatsOpen: Boolean = false,
    val isStatsLoading: Boolean = false,
    val economyStats: EconomyStatsModel? = null,
    val societyStats: SocietyStatsModel? = null,
    val demographyStats: List<DemographyItemModel> = emptyList()
)
