package alejandro.developer.zonaroja.ui.common.snackbar

data class AppSnackbarModel(
    val message: String,
    val type: SnackbarType = SnackbarType.INFO,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)
