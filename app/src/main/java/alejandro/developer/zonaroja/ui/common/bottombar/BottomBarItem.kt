package alejandro.developer.zonaroja.ui.common.bottombar

import alejandro.developer.zonaroja.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomBarItem {
    @get:StringRes
    val labelRes: Int
    val icon: ImageVector

    object Home : BottomBarItem {
        override val labelRes = R.string.bottom_nav_home
        override val icon = Icons.Default.Home
    }

    object Comparison : BottomBarItem {
        override val labelRes = R.string.bottom_nav_comparison
        override val icon = Icons.AutoMirrored.Filled.CompareArrows
    }

    object Favourites : BottomBarItem {
        override val labelRes = R.string.bottom_nav_favourites
        override val icon = Icons.Default.Favorite
    }

    companion object {
        val items = listOf(Home, Comparison, Favourites)
    }
}
