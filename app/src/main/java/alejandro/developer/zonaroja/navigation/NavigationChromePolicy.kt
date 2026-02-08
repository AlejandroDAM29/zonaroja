package alejandro.developer.zonaroja.navigation

import kotlin.reflect.KClass

object NavigationChromePolicy {

    private val noTopBarScreens = setOf(
        Splash::class,
        Login::class
    )

    /*private val bottomBarScreens = setOf(
        Main::class
    )*/

    fun showTopBar(screen: KClass<*>?): Boolean {
        return screen !in noTopBarScreens
    }

    /*fun showBottomBar(screen: KClass<*>?): Boolean {
        return screen in bottomBarScreens
    }*/
}
