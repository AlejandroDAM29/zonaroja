package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.Login
import alejandro.developer.zonaroja.navigation.Main
import alejandro.developer.zonaroja.navigation.Register
import alejandro.developer.zonaroja.navigation.Setting
import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import alejandro.developer.zonaroja.ui.screens.register.RegisterScreen
import alejandro.developer.zonaroja.ui.screens.setting.SettingScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute

fun NavGraphBuilder.authNavGraph(
    navController: NavController
) {

    navigation<AuthGraph>(startDestination = Login()) {
        composable<Login> { navBackStackEntry ->

            val navBackStackEntryLogin: Login = navBackStackEntry.toRoute()

            LoginScreen(
                navigateToMain = {
                    navController.navigate(MainGraph) {
                        popUpTo(AuthGraph) { inclusive = true }
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
                    navController.navigate(MainGraph){
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                }
            )
        }
    }
}