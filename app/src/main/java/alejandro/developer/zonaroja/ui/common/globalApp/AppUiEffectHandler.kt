package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos

@Composable
fun AppUiEffectHandler(
    appViewModel: AppViewModel,
    navigateToLoginLogoutSuccess: () -> Unit
) {

    val appUiController = LocalAppUiController.current


    LaunchedEffect(Unit) {
        appViewModel.uiEffect.collect { effect ->
            when (effect) {

                is AppUiEffect.ShowSnackbarSuccess -> {
                    appUiController.showSnackbarSuccess(effect.message)
                }

                is AppUiEffect.ShowSnackbarError -> {
                    appUiController.showSnackbarError(effect.message)
                }

                is AppUiEffect.ShowSnackbarWarning -> {
                    appUiController.showSnackbarWarning(effect.message)
                }
                is AppUiEffect.NavigateToLoginLogoutSuccess -> {
                    navigateToLoginLogoutSuccess()
                    withFrameNanos { }
                    appUiController.showSnackbarSuccess(effect.message)
                }
            }
        }
    }
}
