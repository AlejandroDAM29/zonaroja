package alejandro.developer.zonaroja.ui.common

import SnackbarController
import alejandro.developer.zonaroja.navigation.NavigationWapper
import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarType
import alejandro.developer.zonaroja.ui.theme.SnackBarInfoColor
import alejandro.developer.zonaroja.ui.theme.SnackBarSuccessColor
import alejandro.developer.zonaroja.ui.theme.SnackbarErrorColor
import alejandro.developer.zonaroja.ui.theme.SnackbarWarningColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow

@Composable
fun AppScaffold(
    appUiEvents: Flow<AppUiEvent>
) {

    val snackbarHostState = remember { SnackbarHostState() }
    //This will be te model that is captured in the snackbar UI event from screen
    var currentSnackbar by remember { mutableStateOf<AppSnackbarModel?>(null) }
    val snackbarController = remember {
        SnackbarController(snackbarHostState)
    }

    LaunchedEffect(snackbarController) {
        snackbarController.currentSnackbar = { snackbar ->
            currentSnackbar = snackbar
        }
    }

    CompositionLocalProvider (
        LocalSnackbarController provides snackbarController
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->

                    val type = currentSnackbar?.type ?: SnackbarType.INFO

                    val backgroundColor = when (type) {
                        SnackbarType.ERROR -> SnackbarErrorColor
                        SnackbarType.WARNING -> SnackbarWarningColor
                        SnackbarType.INFO -> SnackBarInfoColor
                        SnackbarType.SUCCESS -> SnackBarSuccessColor
                    }


                    Snackbar(
                        snackbarData = data,
                        containerColor = backgroundColor,
                        contentColor = Color.White
                    )
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                NavigationWapper()
            }
        }
    }
}
