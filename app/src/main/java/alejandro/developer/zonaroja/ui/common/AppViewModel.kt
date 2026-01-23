package alejandro.developer.zonaroja.ui.common

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {

    private val _uiEvents = MutableSharedFlow<AppUiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    suspend fun emit(event: AppUiEvent) {
        _uiEvents.emit(event)
    }
}
