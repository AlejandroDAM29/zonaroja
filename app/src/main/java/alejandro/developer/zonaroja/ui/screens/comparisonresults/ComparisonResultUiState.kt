package alejandro.developer.zonaroja.ui.screens.comparisonresults

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.ZoneComparisonChartsUiModel

data class ComparisonResultUiState(
    val isLoading: Boolean = true,
    val firstZone: DangerZoneComparisonModel? = null,
    val secondZone: DangerZoneComparisonModel? = null,
    val charts: ZoneComparisonChartsUiModel? = null,
    val comparisonAvailable: Boolean = true
)
