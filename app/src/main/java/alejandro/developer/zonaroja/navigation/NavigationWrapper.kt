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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.compose.currentStateAsState
import kotlin.reflect.KClass

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val appViewmodel: AppViewModel = activityHiltViewModel()
    val unreadNotificationsCount by appViewmodel.unreadNotificationsCount.collectAsState()
    var stableScreen by remember { mutableStateOf<KClass<*>?>(null) }
    var pendingNotificationId by remember { mutableStateOf<Long?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var currentSnackbar by remember { mutableStateOf<AppSnackbarModel?>(null) }

    val snackbarController = remember {
        SnackbarController(snackbarHostState)
    }

    val lifecycleState by backStackEntry
        ?.lifecycle
        ?.currentStateFlow
        ?.collectAsState(initial = Lifecycle.State.INITIALIZED)
        ?: remember { mutableStateOf(Lifecycle.State.INITIALIZED) }

    val currentScreen =
        if (lifecycleState == Lifecycle.State.RESUMED) {
            backStackEntry?.currentScreenType()
        } else {
            stableScreen
        }

    LaunchedEffect(currentScreen) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            stableScreen = currentScreen
        }
    }

    LaunchedEffect(appViewmodel) {
        appViewmodel.notificationOpenRequests.collect { notificationId ->
            pendingNotificationId = notificationId
        }
    }

    LaunchedEffect(currentScreen, stableScreen, pendingNotificationId) {
        val notificationId = pendingNotificationId ?: return@LaunchedEffect
        val resolvedScreen = currentScreen ?: stableScreen ?: return@LaunchedEffect

        if (resolvedScreen !in setOf(
                Splash::class,
                Login::class,
                Register::class,
                ForgotPassword::class
            )
        ) {
            navController.navigate(NotificationDetail(notificationId)) {
                launchSingleTop = true
            }
            pendingNotificationId = null
        }
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
            currentScreen = stableScreen,
            snackbarHostState = snackbarHostState,
            currentSnackbar = currentSnackbar,
            unreadNotificationsCount = unreadNotificationsCount,
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
                            /*popUpTo(MainGraph) {
                                saveState = false
                            }*/
                            launchSingleTop = true/*
                            restoreState = false*/
                        }
                    }

                    DrawerItem.Notifications -> {
                        navController.navigate(Notifications) {
                            launchSingleTop = true
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

                    BottomBarItem.Comparison -> {
                        navController.navigate(ZoneComparisonSelector) {
                            popUpTo(MainGraph) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }

                    BottomBarItem.Favourites -> {
                        navController.navigate(Favourites) {
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

