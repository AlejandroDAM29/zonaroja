package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import alejandro.developer.zonaroja.ui.screens.splash.SplashScreen
import android.util.Log
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
        startDestination = Splash
    ) {

        composable<Splash> {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Login()) {
                        popUpTo(Splash) { inclusive = true }
                    }
                },
                navigateToMain = {
                    navController.navigate(Main) {
                        popUpTo(Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Main> {
            MainScreen(
                onNavigateToLoginLogout = {
                navController.navigate(Login(true)) {
                    popUpTo(Main) { inclusive = true }
                }
            }
            )
        }

        composable<Login> { navBackStackEntry ->

            val navBackStackEntryLogin: Login = navBackStackEntry.toRoute()

            LoginScreen(
                navigateToMain = {
                navController.navigate(Main) {
                    popUpTo(Login()) { inclusive = true }
                }
            },
                successMessage = navBackStackEntryLogin.snackBarMessage
          )
        }
    }
}
