package alejandro.developer.zonaroja.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
data class Main(val showSnackbarRegisterSuccess: Boolean = false)

@Serializable
data class Login(val snackBarMessage: Boolean = false)

@Serializable
object Register