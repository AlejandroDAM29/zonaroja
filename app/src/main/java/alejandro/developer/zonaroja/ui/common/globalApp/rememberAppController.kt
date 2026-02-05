package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAppController(
    appViewModel: AppViewModel
): AppControllerInterface = remember(appViewModel) {
    object : AppControllerInterface {
        override fun showTopBar(title: String) {
            appViewModel.showTopBar(title)
        }

        override fun hideTopBar() {
            appViewModel.hideTopBar()
        }
    }
}
