package alejandro.developer.zonaroja.ui.common.globalApp

import SnackbarController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberAppController(
    snackbarController: SnackbarController
): AppUiController = remember( snackbarController) {
    object : AppUiController {

        //Snackbar events
        override suspend fun showSnackbarError(message: String) {
            snackbarController.showSnackbarError(message = message)
        }

        override suspend fun showSnackbarWarning(message: String) {
            snackbarController.showSnackbarWarning(message = message)
        }

        override suspend fun showSnackbarErrorWithActionButton(
            message: String,
            actionLabel: String,
            onAction: () -> Unit
        ) {
            snackbarController.showSnackbarErrorWithActionButton(
                message = message,
                actionLabel = actionLabel,
                onAction = onAction
            )
        }

        override suspend fun showSnackbarWarningWithActionButton(
            message: String,
            actionLabel: String,
            onAction: () -> Unit
        ) {
            snackbarController.showSnackbarWarningWithActionButton(
                message = message,
                actionLabel = actionLabel,
                onAction = onAction
            )
        }

        override suspend fun showSnackbarSuccess(message: String) {
            snackbarController.showSnackbarSuccess(message = message)
        }
    }
}
