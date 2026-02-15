package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun AppUiEffectHandler(
    appViewModel: AppViewModel,
    navigateToLoginLogout: () -> Unit
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
                AppUiEffect.NavigateToLoginLogout -> {
                    navigateToLoginLogout()
                }
            }
        }
    }
}
