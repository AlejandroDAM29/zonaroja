package alejandro.developer.zonaroja.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class DrawerItem(
    val label: String,
    val icon: ImageVector
) {
    MAIN(
        label = "Zonas peligrosas",
        icon = Icons.Default.Warning
    ),
    STATS(
        label = "Estadísticas",
        icon = Icons.Default.BarChart
    ),
    SETTINGS(
        label = "Ajustes",
        icon = Icons.Default.Settings
    ),
    LOGOUT(
        label = "Cerrar sesión",
        icon = Icons.Default.Logout
    )
}
