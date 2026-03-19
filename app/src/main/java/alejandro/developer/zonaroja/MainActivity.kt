package alejandro.developer.zonaroja

import alejandro.developer.zonaroja.navigation.NavigationWrapper
import alejandro.developer.zonaroja.ui.common.preferences.AppPreferencesViewModel
import alejandro.developer.zonaroja.ui.common.preferences.LocalUserPreferences
import alejandro.developer.zonaroja.ui.theme.ZonarojaTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val appPreferencesViewModel: AppPreferencesViewModel = hiltViewModel()
            val preferences = appPreferencesViewModel.preferences.collectAsStateWithLifecycle()

            ZonarojaTheme(
                darkTheme = preferences.value.darkThemeEnabled,
                dynamicColor = false
            ) {
                CompositionLocalProvider(
                    LocalUserPreferences provides preferences.value
                ) {
                    NavigationWrapper()
                }
            }
        }
    }
}
