package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import alejandro.developer.zonaroja.ui.screens.register.RegisterScreen
import alejandro.developer.zonaroja.ui.screens.splash.SplashScreen
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute


@Composable
fun NavigationWapper() {
    val navController = rememberNavController()

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
    }
}
