package alejandro.developer.zonaroja.ui.screens.main

import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.AppViewModel
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.globalApp.activityHiltViewModel
import alejandro.developer.zonaroja.ui.components.DangerMapContent
import alejandro.developer.zonaroja.ui.components.InfoPanelMap
import alejandro.developer.zonaroja.ui.components.StatisticsBottomSheet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
            appUiEvents.showSnackbarSuccess(
                currentContext.getString(R.string.register_success_snackbar)
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentMainScreen(
    uiState: MainUiState,
    viewModel: MainViewModel,
    appViewModel: AppViewModel
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DangerMapContent(
            isSearcherNameSpacerExpanded = uiState.isSearchExpanded,
            zones = uiState.dangerZonesPoints,
            searchQuery = uiState.searchQuery,
            searchedLocation = uiState.searchedLocation,
            onBoundsChanged = viewModel::onBoundsChanged,
            onSearchQueryChanged = viewModel::onSearchQueryChanged,
            onSearchTriggered = viewModel::searchCity,
            onSearchConsumed = viewModel::clearSearchedLocation,
            onExpandHideClick = viewModel::toggleSearch,
            onOpenPanel = viewModel::openPanel,
            modifier = Modifier.weight(if (uiState.isPanelOpen) 0.6f else 1f)
        )
        if (uiState.isPanelOpen && uiState.selectedZone != null) {

            ModalBottomSheet(
                onDismissRequest = { viewModel.closeBottomSheets() },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {

                InfoPanelMap(
                    zone = uiState.selectedZone,
                    onClose = viewModel::closeBottomSheets,
                    onOpenStats = viewModel::openStats
                )
            }
        }

        if ((uiState.isPanelOpen || uiState.isStatsOpen) &&
            uiState.selectedZone != null
        ) {

            ModalBottomSheet(
                onDismissRequest = { viewModel.closeBottomSheets() },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {

                when {
                    uiState.isPanelOpen -> {
                        InfoPanelMap(
                            zone = uiState.selectedZone,
                            onClose = viewModel::closeBottomSheets,
                            onOpenStats = viewModel::openStats
                        )
                    }

                    uiState.isStatsOpen -> {
                        StatisticsBottomSheet(
                            viewModel
                        )
                    }
                }
            }
        }

    }
}