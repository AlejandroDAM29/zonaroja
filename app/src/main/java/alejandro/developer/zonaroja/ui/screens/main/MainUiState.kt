package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.domain.main.DangerZone

data class MainUiState(
    val currentText: String = "",
    val isLoading: Boolean = false,
    val dangerZonesPoints: List<DangerZone> = emptyList()
)
