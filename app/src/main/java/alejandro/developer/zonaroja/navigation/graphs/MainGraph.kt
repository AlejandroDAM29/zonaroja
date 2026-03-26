package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.ChangePassword
import alejandro.developer.zonaroja.navigation.Favourites
import alejandro.developer.zonaroja.navigation.Main
import alejandro.developer.zonaroja.navigation.REGISTER_SUCCESS_SNACKBAR_KEY
import alejandro.developer.zonaroja.ui.screens.favourites.FavouritesScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.mainNavGraph(
    navController: NavController
) {

    navigation<MainGraph>(startDestination = Main) {

        composable<Main> { backStackEntry ->
            val showSnackbarRegisterSuccess =
                backStackEntry.savedStateHandle.get<Boolean>(REGISTER_SUCCESS_SNACKBAR_KEY) == true

            MainScreen(
                showSnackbarRegisterSuccess = showSnackbarRegisterSuccess,
                onRegisterSuccessSnackbarShown = {
                    backStackEntry.savedStateHandle[REGISTER_SUCCESS_SNACKBAR_KEY] = false
                }
            )
        }

        composable<Favourites> {
            FavouritesScreen()
        }

        settingsNavGraph(navController)
        notificationsNavGraph(navController)
        zoneComparisonNavGraph(navController)
    }
}
