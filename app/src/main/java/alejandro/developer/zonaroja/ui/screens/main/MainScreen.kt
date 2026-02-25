package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.AppViewModel
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.activityHiltViewModel
import alejandro.developer.zonaroja.ui.components.DangerMapContent
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
    showSnackbarRegisterSuccess: Boolean,
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentContext by rememberUpdatedState(LocalContext.current)
    val appUiEvents = LocalAppUiController.current
    val appViewModel: AppViewModel = activityHiltViewModel()

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
            }
        }
    }

    BaseScreen(
        isLoading = uiState.isLoading
    ) {
    ContentMainScreen(
        uiState = uiState,
        viewModel = viewModel,
        appViewModel = appViewModel
    )

    }

}


@Composable
fun ContentMainScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    appViewModel: AppViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DangerMapContent(
            zones = uiState.dangerZonesPoints,
            onBoundsChanged = viewModel::onBoundsChanged,
            modifier = Modifier.weight(1.5f)
        )
        Box(Modifier.weight(0.5f).fillMaxWidth().background(RedClearMap))
    }
}



/*Text(
            text = uiState.currentText,
            modifier = Modifier.clickable {
                viewModel.onTextClicked()
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = appViewModel::onLogoutClicked) {
            Text("Ir a Login")
        }*/