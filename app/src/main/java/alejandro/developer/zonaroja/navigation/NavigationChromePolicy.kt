package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import kotlin.reflect.KClass

object NavigationChromePolicy {

    fun showTopBar(screen: KClass<*>?): Boolean {
        return screen in setOf(
            Main::class,
            Favourites::class,
            ZoneComparisonSelector::class
        )
    }

    fun showBottomBar(screen: KClass<*>?): Boolean {
        return screen in setOf(
            Main::class,
            ZoneComparisonSelector::class,
            Favourites::class
        )
    }

    fun selectedBottomBarItem(screen: KClass<*>?) =
        when (screen) {
            Main::class -> BottomBarItem.Home
            ZoneComparisonSelector::class -> BottomBarItem.Comparison
            Favourites::class -> BottomBarItem.Favourites
            else -> null
        }

    fun isDrawerGestureEnabled(screen: KClass<*>?) =
        screen !in setOf(
            Main::class,
            Notifications::class,
            NotificationDetail::class,
            Login::class,
            Register::class,
            ForgotPassword::class,
            ChangePassword::class,
            Setting::class,
            ZoneComparisonResult::class
        )

}
