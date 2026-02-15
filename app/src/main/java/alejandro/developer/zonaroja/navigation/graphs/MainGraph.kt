package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.Login
import alejandro.developer.zonaroja.navigation.Main
import alejandro.developer.zonaroja.navigation.Setting
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
                showSnackbarRegisterSuccess = args.showSnackbarRegisterSuccess,

                // 🔹 Logout → salir del MainGraph
                onNavigateToLoginLogout = {
                    navController.navigate(AuthGraph) {
                        popUpTo(MainGraph) { inclusive = true }
                    }
                },

                onNavigateToSettings = {
                    navController.navigate(Setting)
                }
            )
        }

        composable<Setting> {
            SettingScreen()
        }
    }
}
