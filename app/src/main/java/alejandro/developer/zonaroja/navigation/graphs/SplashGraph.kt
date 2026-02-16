package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.Splash
import alejandro.developer.zonaroja.ui.screens.splash.SplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

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