package alejandro.developer.zonaroja.ui.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RedCircularProgress() {
    CircularProgressIndicator(
        color = Color(0xFFD32F2F), // rojo
        strokeWidth = 4.dp
    )
}
