package alejandro.developer.zonaroja.ui.common.topbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
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

    object Notifications : DrawerItem {
        override val label = "Notificaciones"
        override val icon = Icons.Default.Notifications
    }

    object Logout : DrawerItem {
        override val label = "Cerrar sesion"
        override val icon = Icons.AutoMirrored.Filled.Logout
    }

    companion object {
        val items = listOf(Main, Notifications, Settings, Logout)
    }
}
