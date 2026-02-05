package alejandro.developer.zonaroja.ui.common.globalApp

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()

    fun showTopBar(title: String) {
        _uiState.update {
            it.copy(
                showTopBar = true,
                topBarTitle = title
            )
        }
    }

    fun hideTopBar() {
        _uiState.update {
            it.copy(showTopBar = false)
        }
    }
}