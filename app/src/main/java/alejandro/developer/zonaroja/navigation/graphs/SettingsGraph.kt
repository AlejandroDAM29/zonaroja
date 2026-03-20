package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.ChangePassword
import alejandro.developer.zonaroja.navigation.Setting
import alejandro.developer.zonaroja.ui.screens.changepassword.ChangePasswordScreen
import alejandro.developer.zonaroja.ui.screens.setting.SettingScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.settingsNavGraph(
    navController: NavController
) {
    navigation<SettingsGraph>(startDestination = Setting) {
        composable<Setting> {
            SettingScreen(
                onClose = { navController.popBackStack() },
                onNavigateToChangePassword = { navController.navigate(ChangePassword) },
                onNavigateToLogin = {
                    navController.navigate(AuthGraph) {
                        popUpTo(MainGraph) { inclusive = true }
                    }
                }
            )
        }

        composable<ChangePassword> {
            ChangePasswordScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
