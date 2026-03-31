package alejandro.developer.zonaroja.navigation

import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationChromePolicyTest {

    @Test
    fun showTopBar_isEnabledForMainScreensOnly() {
        assertTrue(NavigationChromePolicy.showTopBar(Main::class))
        assertFalse(NavigationChromePolicy.showTopBar(Login::class))
    }

    @Test
    fun showBottomBar_isEnabledForBottomNavigationScreensOnly() {
        assertTrue(NavigationChromePolicy.showBottomBar(Favourites::class))
        assertFalse(NavigationChromePolicy.showBottomBar(NotificationDetail::class))
    }

    @Test
    fun selectedBottomBarItem_returnsMatchingItemOrNull() {
        assertEquals(BottomBarItem.Home, NavigationChromePolicy.selectedBottomBarItem(Main::class))
        assertEquals(
            BottomBarItem.Comparison,
            NavigationChromePolicy.selectedBottomBarItem(ZoneComparisonSelector::class)
        )
        assertNull(NavigationChromePolicy.selectedBottomBarItem(Login::class))
    }

    @Test
    fun isDrawerGestureEnabled_isDisabledOnRestrictedScreens() {
        assertFalse(NavigationChromePolicy.isDrawerGestureEnabled(Login::class))
        assertTrue(NavigationChromePolicy.isDrawerGestureEnabled(Favourites::class))
    }
}
