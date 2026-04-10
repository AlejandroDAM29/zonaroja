package alejandro.developer.zonaroja.ui.screens.comparisonselector

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.components.NoInternetCard
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ComparisonSelectorScreen(
    onNavigateToResult: (Int, Int) -> Unit,
    viewModel: ComparisonSelectorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiController = LocalAppUiController.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is ComparisonSelectorUiEvent.NavigateToResult -> {
                    onNavigateToResult(event.firstZoneId, event.secondZoneId)
                }

                is ComparisonSelectorUiEvent.ShowError -> {
                    appUiController.showSnackbarWarning(event.message)
                }
            }
        }
    }

    BaseScreen(isLoading = uiState.isLoading) {
        if (uiState.isOffline) {
            ComparisonOfflineState()
        } else if (uiState.showEmptyState) {
            ComparisonEmptyState()
        } else {
            ComparisonSelectorContent(
                uiState = uiState,
                onFirstZoneSelected = viewModel::onFirstZoneSelected,
                onSecondZoneSelected = viewModel::onSecondZoneSelected,
                onCompareClicked = viewModel::onCompareClicked
            )
        }
    }
}

@Composable
private fun ComparisonOfflineState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        ComparisonHeader(
            title = stringResource(R.string.comparison_selector_title),
            modifier = Modifier.align(Alignment.TopStart)
        )

        NoInternetCard(
            title = stringResource(R.string.no_internet_title),
            description = stringResource(R.string.no_internet_comparison_description),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ComparisonSelectorContent(
    uiState: ComparisonSelectorUiState,
    onFirstZoneSelected: (Int?) -> Unit,
    onSecondZoneSelected: (Int?) -> Unit,
    onCompareClicked: () -> Unit
) {
    val firstSelectedZone =
        uiState.availableZones.firstOrNull { it.id == uiState.firstSelectedZoneId }
    val secondSelectedZone =
        uiState.availableZones.firstOrNull { it.id == uiState.secondSelectedZoneId }

    val firstOptions = uiState.availableZones.filter { it.id != uiState.secondSelectedZoneId }
    val secondOptions = uiState.availableZones.filter { it.id != uiState.firstSelectedZoneId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ComparisonHeader(
            title = stringResource(R.string.comparison_selector_title)
        )

        Text(
            text = stringResource(R.string.comparison_selector_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ZoneSelectorDropdown(
                    title = stringResource(R.string.comparison_first_selector_label),
                    selectedZone = firstSelectedZone,
                    options = firstOptions,
                    onZoneSelected = onFirstZoneSelected
                )

                ZoneSelectorDropdown(
                    title = stringResource(R.string.comparison_second_selector_label),
                    selectedZone = secondSelectedZone,
                    options = secondOptions,
                    onZoneSelected = onSecondZoneSelected
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onCompareClicked,
            enabled = uiState.canCompare,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RedZoneColor,
                contentColor = Color.White,
                disabledContainerColor = RedZoneColor.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.7f)
            )
        ) {
            Text(
                text = stringResource(R.string.comparison_button_label),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoneSelectorDropdown(
    title: String,
    selectedZone: DangerZoneComparisonModel?,
    options: List<DangerZoneComparisonModel>,
    onZoneSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchQuery by remember(
        selectedZone?.id,
        selectedZone?.zoneName,
        selectedZone?.city
    ) {
        mutableStateOf(selectedZone.toDropdownLabel())
    }
    val filteredOptions = remember(options, searchQuery) {
        val normalizedQuery = searchQuery.trim()
        if (normalizedQuery.isBlank()) {
            options
        } else {
            options.filter { zone ->
                zone.zoneName.contains(normalizedQuery, ignoreCase = true) ||
                    zone.city.contains(normalizedQuery, ignoreCase = true) ||
                    zone.toDropdownLabel().contains(normalizedQuery, ignoreCase = true)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { shouldExpand ->
                if (shouldExpand && !expanded && searchQuery == selectedZone.toDropdownLabel()) {
                    searchQuery = ""
                }
                expanded = shouldExpand
            },
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    searchQuery = newValue
                    expanded = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                singleLine = true,
                placeholder = {
                    Text(text = stringResource(R.string.comparison_selector_placeholder))
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RedZoneColor,
                    focusedLabelColor = RedZoneColor
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .heightIn(max = 224.dp)
            ) {
                if (filteredOptions.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(stringResource(R.string.comparison_selector_no_results))
                        },
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )
                } else {
                    filteredOptions.forEachIndexed { index, zone ->
                        DropdownMenuItem(
                            text = {
                                Text(zone.toDropdownLabel())
                            },
                            onClick = {
                                searchQuery = zone.toDropdownLabel()
                                onZoneSelected(zone.id)
                                expanded = false
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        )

                        if (index != filteredOptions.lastIndex) {
                            HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonEmptyState(
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        ComparisonHeader(
            title = stringResource(R.string.comparison_selector_title),
            modifier = Modifier.align(Alignment.TopStart)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = RedZoneColor
                )

                Text(
                    text = stringResource(R.string.comparison_empty_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.comparison_empty_description),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
internal fun ComparisonHeader(
    title: String,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp, top = 8.dp),
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (onClose != null) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.comparison_close_description)
                )
            }
        }
    }
}

private fun DangerZoneComparisonModel?.toDropdownLabel(): String {
    return if (this == null) {
        ""
    } else {
        "$zoneName - $city"
    }
}
