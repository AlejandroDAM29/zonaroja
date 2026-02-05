package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppUiController = staticCompositionLocalOf<AppControllerInterface> {
    error("AppChromeController not provided")
}
