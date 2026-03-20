package alejandro.developer.zonaroja.ui.screens.notifications

import alejandro.developer.domain.usecase.DeleteNotificationUseCase
import alejandro.developer.domain.usecase.ObserveNotificationsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    observeNotificationsUseCase: ObserveNotificationsUseCase,
    private val deleteNotificationUseCase: DeleteNotificationUseCase
) : ViewModel() {

    val uiState = observeNotificationsUseCase()
        .map { notifications ->
            NotificationsUiState(
                notifications = notifications,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationsUiState()
        )

    fun onDeleteClicked(notificationId: Long) {
        viewModelScope.launch {
            deleteNotificationUseCase(notificationId)
        }
    }
}
