package alejandro.developer.zonaroja.ui.screens.comparisonselector

import alejandro.developer.domain.models.DangerZoneComparisonModel

data class ComparisonSelectorUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val availableZones: List<DangerZoneComparisonModel> = emptyList(),
    val firstSelectedZoneId: Int? = null,
    val secondSelectedZoneId: Int? = null
) {
    val canCompare: Boolean
        get() = firstSelectedZoneId != null && secondSelectedZoneId != null

    val showEmptyState: Boolean
        get() = !isLoading && !isOffline && availableZones.isEmpty()
}
