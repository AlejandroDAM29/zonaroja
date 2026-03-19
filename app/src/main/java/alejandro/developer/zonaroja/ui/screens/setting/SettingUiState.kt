package alejandro.developer.zonaroja.ui.screens.setting

import alejandro.developer.domain.models.AppCurrency

data class SettingUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val darkThemeEnabled: Boolean = false,
    val selectedCurrency: AppCurrency = AppCurrency.EUR,
    val notificationsEnabled: Boolean = false,
    val requiresPasswordReauthForDelete: Boolean = false
)
