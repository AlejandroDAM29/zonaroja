package alejandro.developer.zonaroja.ui.common.snackbar

import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarController
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarController =
    staticCompositionLocalOf<SnackbarController> {
        error("SnackbarController not provided")
    }