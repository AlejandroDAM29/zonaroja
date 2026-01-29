package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import alejandro.developer.zonaroja.ui.screens.splash.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute


@Composable
fun NavigationWapper() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                navigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onNavigateToLogin = { message ->
                    navController.navigate("${Routes.LOGIN}?message=$message") {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.LOGIN}?message={message}",
            arguments = listOf(
                navArgument("message") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")

            LoginScreen(
                navigateToMain = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                successMessage = message
            )
        }
    }
}
