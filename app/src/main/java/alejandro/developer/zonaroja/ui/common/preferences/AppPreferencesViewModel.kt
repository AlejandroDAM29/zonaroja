package alejandro.developer.zonaroja.ui.common.preferences

import alejandro.developer.domain.models.UserPreferencesModel
import alejandro.developer.domain.usecase.ObserveUserPreferencesUseCase
import alejandro.developer.domain.usecase.SyncNotificationSubscriptionsUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    observeUserPreferencesUseCase: ObserveUserPreferencesUseCase,
    private val syncNotificationSubscriptionsUseCase: SyncNotificationSubscriptionsUseCase
) : ViewModel() {

    val preferences: StateFlow<UserPreferencesModel> = observeUserPreferencesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferencesModel()
        )

    init {
        viewModelScope.launch {
            syncNotificationSubscriptionsUseCase()
        }
    }
}
