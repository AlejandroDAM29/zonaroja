package alejandro.developer.zonaroja.ui.common.globalApp

interface AppUiController {
    //Snackbar events
    suspend fun showSnackbarError(message: String)
    suspend fun showSnackbarWarning(message: String)
    suspend fun showSnackbarErrorWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit = {}
    )
    suspend fun showSnackbarWarningWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit
    )
    suspend fun showSnackbarSuccess(message: String)

}