package alejandro.developer.zonaroja.ui.common

import alejandro.developer.zonaroja.navigation.NavigationWapper
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow

@Composable
fun AppScaffold(
    appUiEvents: Flow<AppUiEvent>
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarController = remember {
        SnackbarController(snackbarHostState)
    }

    LaunchedEffect(Unit) {
        appUiEvents.collect { event ->
            when (event) {
                is AppUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message
                    )
                }
            }
        }
    }

    CompositionLocalProvider (
        LocalSnackbarController provides snackbarController
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color.Red,
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
