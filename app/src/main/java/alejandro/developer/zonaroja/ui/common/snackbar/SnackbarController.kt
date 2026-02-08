import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarType
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

class SnackbarController(
    private val snackbarHostState: SnackbarHostState
) {

    var currentSnackbar: ((AppSnackbarModel) -> Unit)? = null

    private suspend fun show(snackbar: AppSnackbarModel) {
        currentSnackbar?.invoke(snackbar)

        val result = snackbarHostState.showSnackbar(
            message = snackbar.message,
            actionLabel = snackbar.actionLabel
        )

        if (result == SnackbarResult.ActionPerformed) {
            snackbar.onAction?.invoke()
        }
    }

    suspend fun showSnackbarError(message: String) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.ERROR
            )
        )
    }

    suspend fun showSnackbarWarning(message: String) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.WARNING
            )
        )
    }

    suspend fun showSnackbarErrorWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit = {}
    ) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.ERROR,
                actionLabel = actionLabel,
                onAction = onAction
            )
        )
    }

    suspend fun showSnackbarWarningWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit
    ) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.WARNING,
                actionLabel = actionLabel,
                onAction = onAction
            )
        )
    }

    suspend fun showSnackbarSuccess(
        message: String
    ) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.SUCCESS
            )
        )
    }
}
