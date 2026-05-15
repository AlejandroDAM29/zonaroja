package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem?,
    onItemSelected: (BottomBarItem) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF8E0000)
    ) {
        BottomBarItem.items.forEach { item ->
            val label = stringResource(item.labelRes)
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color(0xFFB71C1C)
                )
            )
        }
    }
}
