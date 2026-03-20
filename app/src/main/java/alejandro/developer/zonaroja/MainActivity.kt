package alejandro.developer.zonaroja

import alejandro.developer.zonaroja.navigation.NavigationWrapper
import alejandro.developer.zonaroja.notifications.NotificationNavigationContract
import alejandro.developer.zonaroja.ui.common.globalApp.AppViewModel
import alejandro.developer.zonaroja.ui.common.preferences.AppPreferencesViewModel
import alejandro.developer.zonaroja.ui.common.preferences.LocalUserPreferences
import alejandro.developer.zonaroja.ui.theme.ZonarojaTheme
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.activity.viewModels
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent(intent)

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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val storedNotificationId = NotificationNavigationContract.consumeStoredNotificationId(intent)
        if (storedNotificationId != null) {
            appViewModel.openStoredNotification(storedNotificationId)
            return
        }

        val incomingNotification = NotificationNavigationContract.consumeIncomingNotification(
            intent = intent,
            fallbackTitle = getString(R.string.app_name)
        ) ?: return

        appViewModel.storeAndOpenNotification(incomingNotification)
    }
}
