package alejandro.developer.zonaroja.ui.common.globalApp

import SnackbarController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberAppController(
    snackbarController: SnackbarController
): AppUiController {
    val snackbarScope = rememberCoroutineScope()

    return remember(snackbarController, snackbarScope) {
    object : AppUiController {

        //Snackbar events
        override fun showSnackbarError(message: String) {
            snackbarScope.launch {
                snackbarController.showSnackbarError(message = message)
            }
        }

        override fun showSnackbarWarning(message: String) {
            snackbarScope.launch {
                snackbarController.showSnackbarWarning(message = message)
            }
        }

        override fun showSnackbarErrorWithActionButton(
            message: String,
            actionLabel: String,
            onAction: () -> Unit
        ) {
            snackbarScope.launch {
                snackbarController.showSnackbarErrorWithActionButton(
                    message = message,
                    actionLabel = actionLabel,
                    onAction = onAction
                )
            }
        }

        override fun showSnackbarWarningWithActionButton(
            message: String,
            actionLabel: String,
            onAction: () -> Unit
        ) {
            snackbarScope.launch {
                snackbarController.showSnackbarWarningWithActionButton(
                    message = message,
                    actionLabel = actionLabel,
                    onAction = onAction
                )
            }
        }

        override fun showSnackbarSuccess(message: String) {
            snackbarScope.launch {
                snackbarController.showSnackbarSuccess(message = message)
            }
        }
    }
}
}
