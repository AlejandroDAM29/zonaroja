package alejandro.developer.zonaroja.navigation

import kotlinx.serialization.Serializable

/*object Routes {
    const val SPLASH = "splash"
    const val MAIN = "main"
    const val LOGIN = "login"
}*/

@Serializable
object Splash

@Serializable
object Main

@Serializable
data class Login(val snackBarMessage: Boolean = false)
