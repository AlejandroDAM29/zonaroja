package alejandro.developer.zonaroja.navigation

import kotlin.reflect.KClass

object NavigationChromePolicy {

    fun showTopBar(screen: KClass<*>?): Boolean {
        return screen in setOf(
            Main::class
        )
    }

    fun showBottomBar(screen: KClass<*>?): Boolean {
        return screen in setOf(
            Main::class,
            Setting::class
        )
    }
}
