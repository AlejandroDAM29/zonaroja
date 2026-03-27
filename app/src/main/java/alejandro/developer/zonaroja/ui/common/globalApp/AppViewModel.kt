package alejandro.developer.zonaroja.ui.common.globalApp

import alejandro.developer.core.network.isNetworkConnectivityError
import alejandro.developer.domain.models.IncomingNotificationModel
import alejandro.developer.domain.usecase.CheckUserSessionUseCase
import alejandro.developer.domain.usecase.GetUserPreferencesUseCase
import alejandro.developer.domain.usecase.LogoutUseCase
import alejandro.developer.domain.usecase.ObserveUnreadNotificationsCountUseCase
import alejandro.developer.domain.usecase.SaveNotificationUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import alejandro.developer.zonaroja.R
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class AppViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val checkUserSessionUseCase: CheckUserSessionUseCase,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    observeUnreadNotificationsCountUseCase: ObserveUnreadNotificationsCountUseCase,
    private val saveNotificationUseCase: SaveNotificationUseCase,
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    sealed interface PendingNotificationRequest {
        data class Stored(val notificationId: Long) : PendingNotificationRequest
        data class Incoming(val notification: IncomingNotificationModel) : PendingNotificationRequest
    }

    private val _uiEffect = MutableSharedFlow<AppUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val _pendingNotificationRequest = MutableStateFlow<PendingNotificationRequest?>(null)
    val pendingNotificationRequest = _pendingNotificationRequest.asStateFlow()

    val unreadNotificationsCount: StateFlow<Int> =
        observeUnreadNotificationsCountUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    fun onLogoutClicked() {
        viewModelScope.launch {
            runCatching { logoutUseCase() }
                .onSuccess {
                    _uiEffect.emit(
                        AppUiEffect.NavigateToLoginLogoutSuccess(
                            message = context.getString(R.string.logout_snackbar_success)
                        )
                    )
                    syncNotificationSubscriptionsAfterNavigation()
                }
                .onFailure { throwable ->
                    _uiEffect.emit(
                        AppUiEffect.ShowSnackbarError(
                            if (throwable.isNetworkConnectivityError()) {
                                context.getString(R.string.error_auth_network)
                            } else {
                                context.getString(R.string.logout_snackbar_error)
                            }
                        )
                    )
                }
        }
    }

    fun showSnackbarSuccess(message: String) {
        viewModelScope.launch {
            _uiEffect.emit(AppUiEffect.ShowSnackbarSuccess(message))
        }
    }

    private suspend fun syncNotificationSubscriptionsAfterNavigation() {
        withContext(NonCancellable) {
            runCatching { syncNotificationSubscriptionsUseCase() }
        }
    }

    fun openStoredNotification(notificationId: Long) {
        _pendingNotificationRequest.value = PendingNotificationRequest.Stored(notificationId)
    }

    fun storeAndOpenNotification(notification: IncomingNotificationModel) {
        _pendingNotificationRequest.value = PendingNotificationRequest.Incoming(notification)
    }

    suspend fun consumePendingNotificationId(): Long? {
        val request = _pendingNotificationRequest.value ?: return null

        if (!checkUserSessionUseCase()) return null
        if (!getUserPreferencesUseCase().notificationsEnabled) {
            _pendingNotificationRequest.value = null
            return null
        }

        val notificationId = when (request) {
            is PendingNotificationRequest.Stored -> request.notificationId
            is PendingNotificationRequest.Incoming -> saveNotificationUseCase(request.notification)
        }

        _pendingNotificationRequest.value = null
        return notificationId
    }

    fun discardPendingNotification() {
        _pendingNotificationRequest.value = null
    }
}
