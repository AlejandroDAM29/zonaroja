package alejandro.developer.zonaroja.ui.common.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface DrawerItem {
    val label: String
    val icon: ImageVector

    object Main : DrawerItem {
        override val label = "Zonas peligrosas"
        override val icon = Icons.Default.Warning
    }

    object Settings : DrawerItem {
        override val label = "Ajustes"
        override val icon = Icons.Default.Settings
    }

    object Logout : DrawerItem {
        override val label = "Cerrar sesión"
        override val icon = Icons.Default.Logout
    }

    companion object {
        val items = listOf(Main, Settings, Logout)
    }
}