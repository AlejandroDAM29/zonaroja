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
object ChangePassword

@Serializable
object Favourites

@Serializable
object ForgotPassword

@Serializable
object ZoneComparisonSelector

@Serializable
data class ZoneComparisonResult(
    val firstZoneId: Int,
    val secondZoneId: Int
)

@Serializable
object Notifications

@Serializable
data class NotificationDetail(
    val notificationId: Long
)
