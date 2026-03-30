package alejandro.developer.zonaroja.ui.screens.notificationdetail

import alejandro.developer.domain.usecase.MarkNotificationAsReadUseCase
import alejandro.developer.domain.usecase.ObserveNotificationByIdUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class NotificationDetailViewModel @Inject constructor(
    private val observeNotificationByIdUseCase: ObserveNotificationByIdUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase
) : ViewModel() {

    private val currentNotificationId = MutableStateFlow<Long?>(null)

    val uiState = currentNotificationId
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { notificationId ->
            observeNotificationByIdUseCase(notificationId).map { notification ->
                NotificationDetailUiState(
                    notification = notification,
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NotificationDetailUiState()
        )

    fun onNotificationOpened(notificationId: Long) {
        if (currentNotificationId.value == notificationId) return

        currentNotificationId.value = notificationId

        viewModelScope.launch {
            markNotificationAsReadUseCase(notificationId)
        }
    }
}
