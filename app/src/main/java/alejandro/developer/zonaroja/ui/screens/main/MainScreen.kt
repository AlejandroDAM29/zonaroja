package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.zonaroja.ui.common.AppUiEvent
import alejandro.developer.zonaroja.ui.common.AppViewModel
import alejandro.developer.zonaroja.ui.common.BaseScreen
import alejandro.developer.zonaroja.ui.common.snackbar.AppSnackbarModel
import alejandro.developer.zonaroja.ui.common.snackbar.LocalSnackbarController
import alejandro.developer.zonaroja.ui.common.snackbar.SnackbarType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarController = LocalSnackbarController.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is MainUiEvent.NavigateToLogin -> onNavigateToLogin()
                is MainUiEvent.ShowError -> {
                    snackbarController.showWarning(event.message)
                }
                is MainUiEvent.ShowWarning -> {
                    snackbarController.showWarning(
                        message = event.message
                    )
                }
            }
        }
    }

    BaseScreen(
        isLoading = uiState.isLoading,
    ) {
    ContentMainScreen(
        uiState = uiState,
        viewModel = viewModel
    )

    }

}


@Composable
fun ContentMainScreen(
    uiState: MainUiState,
    viewModel: MainViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = uiState.currentText,
            modifier = Modifier.clickable {
                viewModel.onTextClicked()
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = viewModel::onLoginClicked) {
            Text("Ir a Login")
        }

    }
}
