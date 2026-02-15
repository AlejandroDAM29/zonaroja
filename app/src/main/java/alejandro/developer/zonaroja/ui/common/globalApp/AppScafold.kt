package alejandro.developer.zonaroja.ui.common.globalApp

import alejandro.developer.zonaroja.navigation.NavigationChromePolicy.selectedBottomBarItem
import alejandro.developer.zonaroja.navigation.NavigationChromePolicy.showBottomBar
import alejandro.developer.zonaroja.navigation.NavigationChromePolicy.showTopBar
import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarType
import alejandro.developer.zonaroja.ui.common.topbar.DrawerItem
import alejandro.developer.zonaroja.ui.components.AppBottomBar
import alejandro.developer.zonaroja.ui.components.AppDrawer
import alejandro.developer.zonaroja.ui.components.AppTopBar
import alejandro.developer.zonaroja.ui.theme.SnackBarInfoColor
import alejandro.developer.zonaroja.ui.theme.SnackBarSuccessColor
import alejandro.developer.zonaroja.ui.theme.SnackbarErrorColor
import alejandro.developer.zonaroja.ui.theme.SnackbarWarningColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Composable
fun AppScaffold(
    currentScreen: KClass<*>?,
    snackbarHostState: SnackbarHostState,
    currentSnackbar: AppSnackbarModel?,
    onDrawerItemSelected: (DrawerItem) -> Unit,
    onBottomItemSelected: (BottomBarItem) -> Unit,
    content: @Composable () -> Unit
) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        modifier = Modifier.Companion.windowInsetsPadding(WindowInsets.Companion.statusBars),
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
                if (showTopBar(currentScreen)) {
                    AppTopBar(
                        title = "Zona Roja",
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            },
            bottomBar = {
                if (showBottomBar(currentScreen)) {
                    AppBottomBar(
                        selectedItem = selectedBottomBarItem(currentScreen),
                        onItemSelected = onBottomItemSelected
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
                        contentColor = Color.Companion.White
                    )
                }
            }
        ) { padding ->
            Box(Modifier.Companion.padding(padding)) {
                content()
            }
        }

    }
}