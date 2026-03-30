package alejandro.developer.zonaroja.ui.common.globalApp

interface AppUiController {
    //Snackbar events
    fun showSnackbarError(message: String)
    fun showSnackbarWarning(message: String)
    fun showSnackbarErrorWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit = {}
    )
    fun showSnackbarWarningWithActionButton(
        message: String,
        actionLabel: String,
        onAction: () -> Unit
    )
    fun showSnackbarSuccess(message: String)

}
