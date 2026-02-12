package alejandro.developer.zonaroja.ui.common.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface BottomBarItem {
    val label: String
    val icon: ImageVector

    object Home : BottomBarItem {
        override val label = "Inicio"
        override val icon = Icons.Default.Home
    }

    object Settings : BottomBarItem {
        override val label = "Configuración"
        override val icon = Icons.Default.Notifications
    }

    companion object {
        val items = listOf(Home, Settings)
    }
}
