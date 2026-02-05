package alejandro.developer.zonaroja.ui.common

import SnackbarController
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.rememberAppController
import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarType
import alejandro.developer.zonaroja.ui.components.AppDrawer
import alejandro.developer.zonaroja.ui.components.AppTopBar
import alejandro.developer.zonaroja.ui.theme.SnackBarInfoColor
import alejandro.developer.zonaroja.ui.theme.SnackBarSuccessColor
import alejandro.developer.zonaroja.ui.theme.SnackbarErrorColor
import alejandro.developer.zonaroja.ui.theme.SnackbarWarningColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun AppScaffold(
    showTopBar: Boolean,
    onDrawerItemSelected: (DrawerItem) -> Unit,
    content: @Composable () -> Unit
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

    val appUiController = rememberAppController(
        snackbarController = snackbarController
    )

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    CompositionLocalProvider(
        LocalAppUiController provides appUiController
    ) {
        ModalNavigationDrawer(
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
            drawerState = drawerState,
            drawerContent = {
                AppDrawer(
                    onItemSelected = {
                        scope.launch { drawerState.close() }
                        onDrawerItemSelected(it)
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    if (showTopBar) {
                        AppTopBar(
                            title = "Zona Roja",
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            }
                        )
                    }
                },
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
                    content()
                }
            }


        }
    }
}
