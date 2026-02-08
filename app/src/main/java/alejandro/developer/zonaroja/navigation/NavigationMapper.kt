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
        route.startsWith(Setting::class.qualifiedName!!) -> Setting::class
        else -> null
    }
}
