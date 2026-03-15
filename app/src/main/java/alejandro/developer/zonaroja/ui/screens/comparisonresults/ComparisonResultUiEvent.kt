package alejandro.developer.zonaroja.ui.screens.comparisonresults

sealed interface ComparisonResultUiEvent {
    data class ShowError(val message: String) : ComparisonResultUiEvent
}
