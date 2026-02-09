package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.navigation.NavigationChromePolicy.showBottomBar
import alejandro.developer.zonaroja.navigation.NavigationChromePolicy.showTopBar
import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import alejandro.developer.zonaroja.ui.common.globalApp.AppScaffold
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = backStackEntry?.currentScreenType()

    val selectedBottomItem = when (currentScreen) {
        Main::class -> BottomBarItem.Home
        Setting::class -> BottomBarItem.Settings
        else -> null
    }

    AppScaffold(
        showTopBar = showTopBar(currentScreen),
        showBottomBar = showBottomBar(currentScreen),
        selectedBottomItem = selectedBottomItem,
        onDrawerItemSelected = { item ->
            when (item) {
                DrawerItem.Main -> {
                    navController.navigate(Main()) {
                        launchSingleTop = true
                        popUpTo(Main()) { inclusive = true }
                    }
                }

                DrawerItem.Settings -> {
                    navController.navigate(Setting)
                }

                DrawerItem.Logout -> {
                    navController.navigate(Login(snackBarMessage = true)) {
                        popUpTo(Main()) { inclusive = true }
                    }
                }
            }
        },
        onBottomItemSelected =  { item ->
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
                        navController.navigate(Login(snackBarMessage = true)) {
                            popUpTo(Main()) { inclusive = true }
                        }
                    },
                    showSnackbarRegisterSuccess = navBackStackEntryLogin.showSnackbarRegisterSuccess
                )
            }

            composable<Login>{ navBackStackEntry ->

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

