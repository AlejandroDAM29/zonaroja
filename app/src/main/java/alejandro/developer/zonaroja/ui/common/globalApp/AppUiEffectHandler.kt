package alejandro.developer.zonaroja.ui.common.globalApp

import alejandro.developer.zonaroja.navigation.Login
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController

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
