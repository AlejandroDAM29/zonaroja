package alejandro.developer.zonaroja

import alejandro.developer.zonaroja.navigation.NavigationWrapper
import alejandro.developer.zonaroja.ui.theme.ZonarojaTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZonarojaTheme {
                NavigationWrapper()
            }
        }
    }
}