package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.ui.common.DrawerItem
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    selectedItem: DrawerItem? = null,
    onItemSelected: (DrawerItem) -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Zona Roja",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Selecciona una categoría",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(color = RedZoneColor)

        DrawerItem.items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                selected = item == selectedItem,
                onClick = { onItemSelected(item) }
            )
        }
    }
}
