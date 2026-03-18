package alejandro.developer.zonaroja.ui.screens.favourites

import alejandro.developer.domain.models.DangerZoneModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.format.formatPricePerSquareMeter
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.preferences.LocalUserPreferences
import alejandro.developer.zonaroja.ui.components.DangerZoneStatisticsSheetContent
import alejandro.developer.zonaroja.ui.components.RiskBadge
import alejandro.developer.zonaroja.ui.components.StatsCard
import alejandro.developer.zonaroja.ui.components.toColor
import alejandro.developer.zonaroja.ui.theme.GreenClearMap
import alejandro.developer.zonaroja.ui.theme.GreenItemFavourite
import alejandro.developer.zonaroja.ui.theme.RedClearMap
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import alejandro.developer.zonaroja.ui.theme.YellowClearMap
import alejandro.developer.zonaroja.ui.theme.YellowItemFavourite
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FavouritesScreen(
    viewModel: FavouritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiController = LocalAppUiController.current

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is FavouritesUiEvent.ShowError -> {
                    appUiController.showSnackbarWarning(event.message)
                }
            }
        }
    }

    BaseScreen(isLoading = uiState.isLoading) {
        FavouritesContent(
            uiState = uiState,
            onZoneClicked = viewModel::onZoneClicked,
            onDeleteClicked = viewModel::onDeleteClicked,
            onOpenStats = viewModel::onOpenStats,
            onCloseStats = viewModel::onCloseStats
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouritesContent(
    uiState: FavouritesUiState,
    onZoneClicked: (Int) -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onOpenStats: (DangerZoneModel) -> Unit,
    onCloseStats: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.favouriteZones.isEmpty() && !uiState.isLoading) {
            EmptyFavouritesState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.favouriteZones,
                    key = DangerZoneModel::id
                ) { zone ->
                    FavouriteZoneCard(
                        zone = zone,
                        isExpanded = uiState.expandedZoneId == zone.id,
                        onExpandToggle = { onZoneClicked(zone.id) },
                        onDeleteClicked = { onDeleteClicked(zone.id) },
                        onOpenStats = { onOpenStats(zone) }
                    )
                }
            }
        }

        if (uiState.isStatsOpen && uiState.selectedZone != null) {
            ModalBottomSheet(
                onDismissRequest = onCloseStats,
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                DangerZoneStatisticsSheetContent(
                    zoneName = uiState.selectedZone.zoneName,
                    cityName = uiState.selectedZone.city,
                    isLoading = uiState.isStatsLoading,
                    economyStats = uiState.economyStats,
                    societyStats = uiState.societyStats,
                    demographyStats = uiState.demographyStats,
                    onBack = onCloseStats,
                    onClose = onCloseStats
                )
            }
        }
    }
}

@Composable
private fun FavouriteZoneCard(
    zone: DangerZoneModel,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onDeleteClicked: () -> Unit,
    onOpenStats: () -> Unit
) {
    val userPreferences = LocalUserPreferences.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onExpandToggle),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = zone.riskLevel.toColorRiskZone(),
                        modifier = Modifier.size(22.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = zone.zoneName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_favourite_description),
                        tint = zone.riskLevel.toColorRiskZone()
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    RiskBadge(zone.riskLevel)

                    Spacer(modifier = Modifier.height(12.dp))

                    FavouriteInfoRow(
                        label = stringResource(R.string.favourites_city_label),
                        value = zone.city
                    )
                    FavouriteInfoRow(
                        label = stringResource(R.string.favourites_price_label),
                        value = formatPricePerSquareMeter(zone.priceSquareMeter, userPreferences)
                    )
                    FavouriteInfoRow(
                        label = stringResource(R.string.favourites_poverty_label),
                        value = "${zone.povertyRiskRate}%"
                    )
                    FavouriteInfoRow(
                        label = stringResource(R.string.favourites_unemployment_label),
                        value = "${zone.unemploymentRate}%"
                    )
                    FavouriteInfoRow(
                        label = stringResource(R.string.favourites_points_label),
                        value = zone.points.size.toString()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    StatsCard(zone)

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onOpenStats,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedZoneColor,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.see_stadistics_button))
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavouriteInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyFavouritesState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = RedZoneColor,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.favourites_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.favourites_empty_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun RiskLevel.toColorRiskZone(): Color {
    return when (this) {
        RiskLevel.LOW -> GreenItemFavourite
        RiskLevel.MEDIUM -> YellowItemFavourite
        RiskLevel.HIGH -> RedZoneColor
    }
}
