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

    suspend fun showError(message: String) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.ERROR
            )
        )
    }

    suspend fun showWarning(message: String) {
        show(
            AppSnackbarModel(
                message = message,
                type = SnackbarType.WARNING
            )
        )
    }

    suspend fun showErrorWithActionButton(
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

    suspend fun showWarningWithButton(
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

    suspend fun showSuccess(
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
