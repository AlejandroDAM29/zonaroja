package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import kotlin.reflect.KClass

object NavigationChromePolicy {

    fun showTopBar(screen: KClass<*>?): Boolean {
        return screen in setOf(
            Main::class,
            Setting::class
        )
    }

    fun showBottomBar(screen: KClass<*>?): Boolean {
        return screen in setOf(
            Main::class,
            Setting::class
        )
    }

    fun selectedBottomBarItem(screen: KClass<*>?) =
        when (screen) {
            Main::class -> BottomBarItem.Home
            Setting::class -> BottomBarItem.Settings
            else -> null
        }

}
