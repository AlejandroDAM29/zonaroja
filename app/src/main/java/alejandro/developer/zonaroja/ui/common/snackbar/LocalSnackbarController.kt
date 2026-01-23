package alejandro.developer.zonaroja.ui.common.snackbar

import SnackbarController
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarController =
    staticCompositionLocalOf<SnackbarController> {
        error("SnackbarController not provided")
    }