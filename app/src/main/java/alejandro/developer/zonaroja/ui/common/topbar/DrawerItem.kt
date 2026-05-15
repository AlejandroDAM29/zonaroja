package alejandro.developer.zonaroja.ui.common.topbar

import alejandro.developer.zonaroja.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed interface DrawerItem {
    @get:StringRes
    val labelRes: Int
    val icon: ImageVector

    object Main : DrawerItem {
        override val labelRes = R.string.drawer_main
        override val icon = Icons.Default.Warning
    }

    object Settings : DrawerItem {
        override val labelRes = R.string.drawer_settings
        override val icon = Icons.Default.Settings
    }

    object Notifications : DrawerItem {
        override val labelRes = R.string.settings_notifications
        override val icon = Icons.Default.Notifications
    }

    object Logout : DrawerItem {
        override val labelRes = R.string.settings_logout
        override val icon = Icons.AutoMirrored.Filled.Logout
    }

    companion object {
        val items = listOf(Main, Notifications, Settings, Logout)
    }
}
