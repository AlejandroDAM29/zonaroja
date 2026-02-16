package alejandro.developer.zonaroja.ui.components

import alejandro.developer.zonaroja.ui.common.bottombar.BottomBarItem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppBottomBar(
    selectedItem: BottomBarItem?,
    onItemSelected: (BottomBarItem) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF8E0000)
    ) {
        BottomBarItem.items.forEach { item ->
            NavigationBarItem(
                selected = item == selectedItem,
                onClick = { onItemSelected(item) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(item.label)
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
