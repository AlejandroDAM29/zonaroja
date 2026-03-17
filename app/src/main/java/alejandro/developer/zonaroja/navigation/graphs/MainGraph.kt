package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.ChangePassword
import alejandro.developer.zonaroja.navigation.Favourites
import alejandro.developer.zonaroja.navigation.Main
import alejandro.developer.zonaroja.navigation.Setting
import alejandro.developer.zonaroja.ui.screens.favourites.FavouritesScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import alejandro.developer.zonaroja.ui.screens.setting.SettingScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.mainNavGraph(
    navController: NavController
) {

    navigation<MainGraph>(startDestination = Main()) {

        composable<Main> { backStackEntry ->

            val args: Main = backStackEntry.toRoute()

            MainScreen(
                showSnackbarRegisterSuccess = args.showSnackbarRegisterSuccess
            )
        }

        composable<Setting> {
            SettingScreen(
                onClose = {
                    navController.popBackStack()
                          },
                onNavigateToChangePassword = { navController.navigate(ChangePassword) },
                onNavigateToLogin = {
                    navController.navigate(AuthGraph) {
                        popUpTo(MainGraph) { inclusive = true }
                    }
                }
            )
        }

        zoneComparisonNavGraph(navController)
    }
}
