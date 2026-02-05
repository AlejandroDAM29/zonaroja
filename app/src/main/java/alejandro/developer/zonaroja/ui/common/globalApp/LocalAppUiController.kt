package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.compose.runtime.staticCompositionLocalOf

val LocalAppUiController = staticCompositionLocalOf<AppUiController> {
    error("AppChromeController not provided")
}
