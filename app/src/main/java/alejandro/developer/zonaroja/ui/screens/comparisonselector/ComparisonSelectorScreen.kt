package alejandro.developer.zonaroja.ui.screens.comparisonselector

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
        if (uiState.showEmptyState) {
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
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedZone.toDropdownLabel(),
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                options.forEachIndexed { index, zone ->
                    DropdownMenuItem(
                        text = {
                            Text(zone.toDropdownLabel())
                        },
                        onClick = {
                            onZoneSelected(zone.id)
                            expanded = false
                        },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )

                    if (index != options.lastIndex) {
                        HorizontalDivider(Modifier.fillMaxWidth(), thickness = 1.dp)
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
