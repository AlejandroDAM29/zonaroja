package alejandro.developer.zonaroja.navigation

import SnackbarController
import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import alejandro.developer.zonaroja.ui.common.globalApp.AppScaffold
import alejandro.developer.zonaroja.ui.common.globalApp.AppUiEffectHandler
import alejandro.developer.zonaroja.ui.common.globalApp.AppViewModel
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.rememberAppController
import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.topbar.DrawerItem
import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import alejandro.developer.zonaroja.ui.screens.register.RegisterScreen
import alejandro.developer.zonaroja.ui.screens.setting.SettingScreen
import alejandro.developer.zonaroja.ui.screens.splash.SplashScreen
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val appViewmodel: AppViewModel = hiltViewModel()
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
            navigateToLoginLogout = {
                navController.navigate(Login()) {
                    popUpTo(Main()) { inclusive = true }
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
                            popUpTo(Main()) {
                                inclusive = false
                            }
                            launchSingleTop = true
                        }
                    }

                    DrawerItem.Settings -> {
                        navController.navigate(Setting) {
                            popUpTo(Main()) {
                                inclusive = false
                            }
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
                    BottomBarItem.Home -> navController.navigate(Main()) {
                        popUpTo(Main()) { inclusive = true }
                    }

                    BottomBarItem.Settings -> navController.navigate(Setting) {
                        popUpTo(Setting) { inclusive = true }
                    }
                }
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = Splash,
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

                composable<Splash> {
                    SplashScreen(
                        navigateToLogin = {
                            navController.navigate(Login()) {
                                popUpTo(Splash) { inclusive = true }
                            }
                        },
                        navigateToMain = {
                            navController.navigate(Main()) {
                                popUpTo(Splash) { inclusive = true }
                            }
                        }
                    )
                }

                composable<Main> { navBackStackEntry ->

                    val navBackStackEntryLogin: Main = navBackStackEntry.toRoute()

                    MainScreen(
                        onNavigateToLoginLogout = {
                            navController.navigate(Login(snackBarMessage = true)){
                                popUpTo(Main()) { inclusive = true }
                            }
                        },
                        onNavigateToSettings ={
                            navController.navigate(Setting) {
                                popUpTo(Main()) { inclusive = true }
                            }
                        },
                        showSnackbarRegisterSuccess = navBackStackEntryLogin.showSnackbarRegisterSuccess
                    )
                }

                composable<Login> { navBackStackEntry ->

                    val navBackStackEntryLogin: Login = navBackStackEntry.toRoute()

                    LoginScreen(
                        navigateToMain = {
                            navController.navigate(Main()) {
                                popUpTo(Login()) { inclusive = true }
                            }
                        },
                        navigateToRegister = { navController.navigate(Register) },
                        successMessage = navBackStackEntryLogin.snackBarMessage,
                    )
                }

                composable<Register> {
                    RegisterScreen(
                        onBackToLogin = {
                            navController.popBackStack()
                        },
                        onNavigateToMain = {
                            navController.navigate(Main(showSnackbarRegisterSuccess = true))
                        }
                    )
                }

                composable<Setting> {
                    SettingScreen()
                }
            }
        }
    }
}

