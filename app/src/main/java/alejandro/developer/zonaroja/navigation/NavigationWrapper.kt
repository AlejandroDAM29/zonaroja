package alejandro.developer.zonaroja.navigation

import SnackbarController
import alejandro.developer.zonaroja.navigation.graphs.AuthGraph
import alejandro.developer.zonaroja.navigation.graphs.MainGraph
import alejandro.developer.zonaroja.navigation.graphs.SplashGraph
import alejandro.developer.zonaroja.navigation.graphs.authNavGraph
import alejandro.developer.zonaroja.navigation.graphs.mainNavGraph
import alejandro.developer.zonaroja.navigation.graphs.splashNavGraph
import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import alejandro.developer.zonaroja.ui.common.globalApp.AppScaffold
import alejandro.developer.zonaroja.ui.common.globalApp.AppUiEffectHandler
import alejandro.developer.zonaroja.ui.common.globalApp.AppViewModel
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.activityHiltViewModel
import alejandro.developer.zonaroja.ui.common.globalApp.rememberAppController
import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.topbar.DrawerItem
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val appViewmodel: AppViewModel = activityHiltViewModel()
    val currentScreen = backStackEntry?.currentScreenType()
    val snackbarHostState = remember { SnackbarHostState() }
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

    CompositionLocalProvider(
        LocalAppUiController provides appUiController
    ) {
        AppUiEffectHandler(
            appViewModel = appViewmodel,
            navigateToLoginLogoutSuccess = {
                navController.navigate(AuthGraph) {
                    popUpTo(MainGraph) { inclusive = true }
                }
            }
        )

        AppScaffold(
            currentScreen = currentScreen,
            snackbarHostState = snackbarHostState,
            currentSnackbar = currentSnackbar,
            onDrawerItemSelected = { item ->
                when (item) {
                    DrawerItem.Main -> {
                        navController.navigate(Main()) {
                            popUpTo(MainGraph) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    DrawerItem.Settings -> {
                        navController.navigate(Setting) {
                            popUpTo(MainGraph) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    DrawerItem.Logout -> {
                        appViewmodel.onLogoutClicked()
                    }
                }
            },
            onBottomItemSelected = { item ->
                when (item) {
                    BottomBarItem.Home -> {
                        navController.navigate(Main()) {
                            popUpTo(MainGraph) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    BottomBarItem.Settings -> {
                        navController.navigate(Setting) {
                            popUpTo(MainGraph) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = SplashGraph,
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            400, easing = LinearEasing
                        )
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(
                            400, easing = LinearEasing
                        )
                    )
                }
            ) {
                splashNavGraph(navController)
                authNavGraph(navController)
                mainNavGraph(navController)
            }
        }
    }
}

