package alejandro.developer.zonaroja.ui.screens.notifications

import alejandro.developer.domain.models.AppNotificationModel

data class NotificationDetailUiState(
    val notification: AppNotificationModel? = null,
    val isLoading: Boolean = true
)
