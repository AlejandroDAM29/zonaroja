package alejandro.developer.zonaroja.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF7D7D),
    onPrimary = Color(0xFF3A0000),
    secondary = Color(0xFFFFB3B3),
    onSecondary = Color(0xFF321111),
    tertiary = Color(0xFFF2C7C7),
    background = Color(0xFF120C0F),
    onBackground = Color(0xFFF8EDEE),
    surface = Color(0xFF21161A),
    onSurface = Color(0xFFF8EDEE),
    surfaceVariant = Color(0xFF3A2A30),
    onSurfaceVariant = Color(0xFFE9D5D9),
    outline = Color(0xFF80636B)
)

private val LightColorScheme = lightColorScheme(
    primary = RedZoneColor,
    onPrimary = White,
    secondary = Color(0xFFFFD5D8),
    onSecondary = Color(0xFF5C2025),
    tertiary = Color(0xFFF8EAEA),
    background = Color(0xFFF8F0F1),
    onBackground = Color(0xFF4A3A3D),
    surface = White,
    onSurface = Color(0xFF4A3A3D),
    surfaceVariant = Color(0xFFF4E2E4),
    onSurfaceVariant = Color(0xFF7B666D),
    outline = Color(0xFFD6BCC2)
)

@Composable
fun ZonarojaTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
