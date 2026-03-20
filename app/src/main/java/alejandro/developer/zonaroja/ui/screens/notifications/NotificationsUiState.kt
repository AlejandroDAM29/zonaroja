package alejandro.developer.zonaroja.ui.screens.notifications

import alejandro.developer.domain.models.AppNotificationModel

data class NotificationsUiState(
    val notifications: List<AppNotificationModel> = emptyList(),
    val isLoading: Boolean = true
)
