package alejandro.developer.zonaroja.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
data class Main(val showSnackbarRegisterSuccess: Boolean = false)

@Serializable
object Login

@Serializable
object Register

@Serializable
object Setting

@Serializable
object Favourites

@Serializable
object ForgotPassword
