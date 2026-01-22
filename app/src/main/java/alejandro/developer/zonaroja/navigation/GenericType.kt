@file:Suppress("DEPRECATION")

package alejandro.developer.zonaroja.navigation

import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.json.Json

inline fun <reified T: Parcelable> createNavType(): NavType<T>{
    return object : NavType<T>(isNullableAllowed = true){
        override fun put(bundle: SavedState, key: String, value: T) {
            bundle.putParcelable(key, value)
        }

        override fun get(bundle: SavedState, key: String): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, T::class.java)
            } else {
                bundle.getParcelable(key)
            }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString<T>(value)
        }

        override fun serializeAsValue(value: T): String {
            return Uri.encode(Json.encodeToString(value))
        }

    }
}