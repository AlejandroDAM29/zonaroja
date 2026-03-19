package alejandro.developer.zonaroja.ui.common.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomBarItem {
    val label: String
    val icon: ImageVector

    object Home : BottomBarItem {
        override val label = "Inicio"
        override val icon = Icons.Default.Home
    }

    object Comparison : BottomBarItem {
        override val label = "Comparador"
        override val icon = Icons.AutoMirrored.Filled.CompareArrows
    }

    object Favourites : BottomBarItem {
        override val label = "Favoritos"
        override val icon = Icons.Default.Favorite
    }

    companion object {
        val items = listOf(Home, Comparison, Favourites)
    }
}
