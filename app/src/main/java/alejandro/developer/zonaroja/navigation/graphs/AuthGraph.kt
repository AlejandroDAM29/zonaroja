package alejandro.developer.zonaroja.navigation.graphs

import alejandro.developer.zonaroja.navigation.ForgotPassword
import alejandro.developer.zonaroja.navigation.Login
import alejandro.developer.zonaroja.navigation.Main
import alejandro.developer.zonaroja.navigation.Register
import alejandro.developer.zonaroja.navigation.REGISTER_SUCCESS_SNACKBAR_KEY
import alejandro.developer.zonaroja.ui.screens.forgotpassword.ForgotPasswordScreen
import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.register.RegisterScreen
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.authNavGraph(
    navController: NavController
) {

    navigation<AuthGraph>(startDestination = Login) {
        composable<Login> {

            LoginScreen(
                navigateToMain = {
                    navController.navigate(MainGraph) {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                },
                navigateToRegister = { navController.navigate(Register) },
                navigateToForgotPassword = { navController.navigate(ForgotPassword) }
            )
        }

        composable<Register> {

            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onNavigateToMain = {
                    navController.navigate(Main) {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set(REGISTER_SUCCESS_SNACKBAR_KEY, true)
                }
            )
        }

        composable<ForgotPassword> {

            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

    }
}
