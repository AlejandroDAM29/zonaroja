package alejandro.developer.zonaroja.ui.screens.comparisonresults

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.format.formatPricePerSquareMeter
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.common.preferences.LocalUserPreferences
import alejandro.developer.zonaroja.ui.components.RiskBadge
import alejandro.developer.zonaroja.ui.components.ZoneComparisonBarChartCard
import alejandro.developer.zonaroja.ui.components.ZoneComparisonRiskChartCard
import alejandro.developer.zonaroja.ui.screens.comparisonselector.ComparisonHeader
import alejandro.developer.domain.models.ZoneComparisonChartsUiModel
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ComparisonResultScreen(
    firstZoneId: Int,
    secondZoneId: Int,
    onBack: () -> Unit,
    onClose: () -> Unit,
    viewModel: ComparisonResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiController = LocalAppUiController.current
    val firstZone = uiState.firstZone
    val secondZone = uiState.secondZone
    val charts = uiState.charts

    BackHandler(onBack = onBack)

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is ComparisonResultUiEvent.ShowError -> {
                    appUiController.showSnackbarWarning(event.message)
                }
            }
        }
    }

    LaunchedEffect(firstZoneId, secondZoneId) {
        viewModel.loadComparison(
            firstZoneId = firstZoneId,
            secondZoneId = secondZoneId
        )
    }

    BaseScreen(isLoading = uiState.isLoading) {
        when {
            uiState.isLoading && firstZone == null && secondZone == null && charts == null -> {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                )
            }

            !uiState.comparisonAvailable -> {
                ComparisonUnavailableState(
                    onBack = onBack,
                    onClose = onClose
                )
            }

            firstZone != null && secondZone != null && charts != null -> {
                ComparisonResultContent(
                    firstZone = firstZone,
                    secondZone = secondZone,
                    charts = charts,
                    onClose = onClose
                )
            }
        }
    }
}

@Composable
private fun ComparisonResultContent(
    firstZone: DangerZoneComparisonModel,
    secondZone: DangerZoneComparisonModel,
    charts: ZoneComparisonChartsUiModel,
    onClose: () -> Unit
) {
    val userPreferences = LocalUserPreferences.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ComparisonHeader(
                title = stringResource(R.string.comparison_result_title),
                onClose = onClose
            )
        }

        item {
            Text(
                text = stringResource(R.string.comparison_result_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val textMeasurer = rememberTextMeasurer()
                val density = LocalDensity.current
                val titleStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                val cardInnerHorizontalPadding = 18.dp * 2
                val cardSpacing = 12.dp
                val cardWidth = (maxWidth - cardSpacing) / 2
                val titleMaxWidthPx = with(density) {
                    ((cardWidth - cardInnerHorizontalPadding).value * this.density).toInt()
                }.coerceAtLeast(0)
                val shouldReserveTwoLines = remember(
                    firstZone.zoneName,
                    secondZone.zoneName,
                    titleStyle,
                    titleMaxWidthPx
                ) {
                    requiresTwoTitleLines(
                        text = firstZone.zoneName,
                        style = titleStyle,
                        maxWidthPx = titleMaxWidthPx,
                        textMeasurer = textMeasurer
                    ) || requiresTwoTitleLines(
                        text = secondZone.zoneName,
                        style = titleStyle,
                        maxWidthPx = titleMaxWidthPx,
                        textMeasurer = textMeasurer
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ComparisonZoneSummaryCard(
                        zone = firstZone,
                        reserveTitleTwoLines = shouldReserveTwoLines,
                        userPreferences = userPreferences,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    ComparisonZoneSummaryCard(
                        zone = secondZone,
                        reserveTitleTwoLines = shouldReserveTwoLines,
                        userPreferences = userPreferences,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }

        item {
            ZoneComparisonRiskChartCard(chart = charts.riskChart)
        }

        items(
            items = charts.metricCharts,
            key = { it.metricType.name }
        ) { chart ->
            ZoneComparisonBarChartCard(chart = chart)
        }
    }
}

@Composable
private fun ComparisonZoneSummaryCard(
    zone: DangerZoneComparisonModel,
    reserveTitleTwoLines: Boolean,
    userPreferences: alejandro.developer.domain.models.UserPreferencesModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = zone.zoneName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                minLines = if (reserveTitleTwoLines) 2 else 1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = zone.city,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            RiskBadge(zone.riskLevel)

            ComparisonSummaryLine(
                label = stringResource(R.string.comparison_metric_poverty),
                value = "${zone.povertyRiskRate}%"
            )
            ComparisonSummaryLine(
                label = stringResource(R.string.comparison_metric_unemployment),
                value = "${zone.unemploymentRate}%"
            )
            ComparisonSummaryLine(
                label = stringResource(R.string.comparison_metric_price),
                value = formatPricePerSquareMeter(zone.priceSquareMeter, userPreferences)
            )
        }
    }
}

@Composable
private fun ComparisonSummaryLine(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
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

private fun requiresTwoTitleLines(
    text: String,
    style: TextStyle,
    maxWidthPx: Int,
    textMeasurer: TextMeasurer
): Boolean {
    if (maxWidthPx <= 0) return false

    val result = textMeasurer.measure(
        text = text,
        style = style,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        constraints = Constraints(maxWidth = maxWidthPx)
    )

    return result.lineCount > 1
}

@Composable
private fun ComparisonUnavailableState(
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ComparisonHeader(
            title = stringResource(R.string.comparison_result_title),
            onClose = onClose
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.comparison_unavailable_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.comparison_unavailable_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedZoneColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )

                    Text(
                        text = stringResource(R.string.comparison_back_to_selector),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
