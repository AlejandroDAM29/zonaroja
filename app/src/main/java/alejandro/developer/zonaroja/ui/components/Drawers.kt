package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.topbar.DrawerItem
import alejandro.developer.zonaroja.ui.theme.Black
import alejandro.developer.zonaroja.ui.theme.GreaseHorizontalDivider
import alejandro.developer.zonaroja.ui.theme.GreaseTextFieldText
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                text = stringResource(R.string.zona_roja_title),
                style = MaterialTheme.typography.headlineSmall,
                color = RedZoneColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.select_category),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider(color = RedZoneColor)

        DrawerItem.items.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedIconColor = RedZoneColor,
                    unselectedIconColor = RedZoneColor
                ),
                selected = item == selectedItem,
                onClick = { onItemSelected(item) }
            )
            if (index < DrawerItem.items.lastIndex)
                HorizontalDivider(
                    color = GreaseHorizontalDivider,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
        }
    }
}
