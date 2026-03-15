package alejandro.developer.zonaroja.ui.screens.comparisonselector

sealed interface ComparisonSelectorUiEvent {
    data class ShowError(val message: String) : ComparisonSelectorUiEvent
    data class NavigateToResult(
        val firstZoneId: Int,
        val secondZoneId: Int
    ) : ComparisonSelectorUiEvent
}
