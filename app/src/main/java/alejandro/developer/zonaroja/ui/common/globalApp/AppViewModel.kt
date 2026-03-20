package alejandro.developer.zonaroja.ui.common.globalApp

import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.usecase.GetUserPreferencesUseCase
import alejandro.developer.domain.usecase.LogoutUseCase
import alejandro.developer.domain.usecase.ObserveUnreadNotificationsCountUseCase
import alejandro.developer.domain.usecase.SaveNotificationUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    observeUnreadNotificationsCountUseCase: ObserveUnreadNotificationsCountUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase
) : ViewModel() {

    private val _uiEffect = MutableSharedFlow<AppUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val _notificationOpenRequests = MutableSharedFlow<Long>(extraBufferCapacity = 1)
    val notificationOpenRequests = _notificationOpenRequests.asSharedFlow()

    val unreadNotificationsCount: StateFlow<Int> =
        observeUnreadNotificationsCountUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    fun onLogoutClicked() {
        viewModelScope.launch {
            try {
                logoutUseCase()

                _uiEffect.emit(AppUiEffect.NavigateToLoginLogoutSuccess(
                    message = "Logout correcto"
                )
                )

            } catch (e: Exception) {

                _uiEffect.emit(
                    AppUiEffect.ShowSnackbarError("Error al cerrar sesión")
                )
            }
        }
    }

    fun openStoredNotification(notificationId: Long) {
        viewModelScope.launch {
            if (!getUserPreferencesUseCase().notificationsEnabled) return@launch
            _notificationOpenRequests.emit(notificationId)
        }
    }

    fun storeAndOpenNotification(notification: IncomingNotificationModel) {
        viewModelScope.launch {
            if (!getUserPreferencesUseCase().notificationsEnabled) return@launch
            val notificationId = saveNotificationUseCase(notification)
            _notificationOpenRequests.emit(notificationId)
        }
    }
}
