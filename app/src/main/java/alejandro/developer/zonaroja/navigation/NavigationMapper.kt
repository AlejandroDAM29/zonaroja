package alejandro.developer.zonaroja.navigation

import androidx.navigation.NavBackStackEntry
import kotlin.reflect.KClass


fun NavBackStackEntry.currentScreenType(): KClass<*>? {
    val route = destination.route ?: return null

    return when {
        route.startsWith(Splash::class.qualifiedName!!) -> Splash::class
        route.startsWith(Login::class.qualifiedName!!) -> Login::class
        route.startsWith(Main::class.qualifiedName!!) -> Main::class
        route.startsWith(Register::class.qualifiedName!!) -> Register::class
        route.startsWith(ForgotPassword::class.qualifiedName!!) -> ForgotPassword::class
        route.startsWith(Setting::class.qualifiedName!!) -> Setting::class
        route.startsWith(ChangePassword::class.qualifiedName!!) -> ChangePassword::class
        route.startsWith(Favourites::class.qualifiedName!!) -> Favourites::class
        route.startsWith(Notifications::class.qualifiedName!!) -> Notifications::class
        route.startsWith(NotificationDetail::class.qualifiedName!!) -> NotificationDetail::class
        route.startsWith(ZoneComparisonSelector::class.qualifiedName!!) -> ZoneComparisonSelector::class
        route.startsWith(ZoneComparisonResult::class.qualifiedName!!) -> ZoneComparisonResult::class
        else -> null
    }
}
