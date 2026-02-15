package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.Login
import alejandro.developer.zonaroja.navigation.Main
import alejandro.developer.zonaroja.navigation.Splash
import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.splash.SplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.splashNavGraph(
    navController: NavController
) {

    navigation<SplashGraph>(startDestination = Splash) {
        composable<Splash> {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(AuthGraph) {
                        popUpTo(SplashGraph) { inclusive = true }
                    }
                },
                navigateToMain = {
                    navController.navigate(MainGraph) {
                        popUpTo(SplashGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}