package alejandro.developer.zonaroja.ui.common.snackbar

import androidx.compose.material3.SnackbarHostState

class SnackbarController(
    private val snackbarHostState: SnackbarHostState
) {
    suspend fun showError(message: String) {
        snackbarHostState.showSnackbar(message)
    }
}