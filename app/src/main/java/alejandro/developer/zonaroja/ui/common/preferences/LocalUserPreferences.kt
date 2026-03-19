package alejandro.developer.zonaroja.ui.common.preferences

import alejandro.developer.domain.models.UserPreferencesModel
import androidx.compose.runtime.compositionLocalOf

val LocalUserPreferences = compositionLocalOf { UserPreferencesModel() }
