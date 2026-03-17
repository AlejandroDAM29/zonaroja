package alejandro.developer.zonaroja

import alejandro.developer.zonaroja.notifications.NotificationChannelManager
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ZonaRojaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannelManager.ensureGeneralChannel(this)
    }
}
