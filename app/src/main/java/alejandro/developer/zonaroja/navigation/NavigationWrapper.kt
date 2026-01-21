package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.screens.login.LoginScreen
import alejandro.developer.zonaroja.ui.screens.main.MainScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationWapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Main){
        composable<Main>{
            MainScreen({navController.navigate(Login)})
        }

        composable<Login>{
            LoginScreen({navController.navigate(Main)})
        }
    }
}
