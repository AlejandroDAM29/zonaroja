package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MainScreen(
    onNavigateToLoginLogout: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    showSnackbarRegisterSuccess: Boolean,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentContext by rememberUpdatedState(LocalContext.current)
    val appUiEvents = LocalAppUiController.current

    LaunchedEffect(showSnackbarRegisterSuccess) {
        if (showSnackbarRegisterSuccess)
            appUiEvents.showSnackbarSuccess(currentContext.getString(R.string.register_success_snackbar)
            )
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is MainUiEvent.ShowError -> {
                    appUiEvents.showSnackbarWarning(event.message)
                }
                is MainUiEvent.ShowWarning -> {
                    appUiEvents.showSnackbarWarning(
                        message = event.message
                    )
                }
                is MainUiEvent.ShowLogoutSuccessAndNavigateToLogin -> {
                    onNavigateToSettings()
                }
                is MainUiEvent.ShowLogoutError -> {
                    appUiEvents.showSnackbarError(currentContext.getString(R.string.logout_snackbar_error))
                }
            }
        }
    }

    BaseScreen(
        isLoading = uiState.isLoading
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

        Button(onClick = viewModel::onLogoutClicked) {
            Text("Ir a Login")
        }

    }
}
