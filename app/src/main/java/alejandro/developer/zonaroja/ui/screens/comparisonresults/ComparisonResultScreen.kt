package alejandro.developer.zonaroja.ui.screens.comparisonresults

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.zonaroja.R
import alejandro.developer.zonaroja.ui.common.globalApp.BaseScreen
import alejandro.developer.zonaroja.ui.common.globalApp.LocalAppUiController
import alejandro.developer.zonaroja.ui.components.RiskBadge
import alejandro.developer.zonaroja.ui.components.ZoneComparisonMetricChartCard
import alejandro.developer.zonaroja.ui.components.ZoneComparisonRiskChartCard
import alejandro.developer.zonaroja.ui.screens.comparisonselector.ComparisonHeader
import alejandro.developer.domain.models.ZoneComparisonChartsUiModel
import alejandro.developer.zonaroja.ui.theme.RedZoneColor
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        if (!uiState.comparisonAvailable || firstZone == null || secondZone == null || charts == null) {
            ComparisonUnavailableState(
                onBack = onBack,
                onClose = onClose
            )
        } else {
            ComparisonResultContent(
                firstZone = firstZone,
                secondZone = secondZone,
                charts = charts,
                onClose = onClose
            )
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4F1))
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
                color = Color(0xFF6E5B55)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ComparisonZoneSummaryCard(
                    zone = firstZone,
                    modifier = Modifier.weight(1f)
                )
                ComparisonZoneSummaryCard(
                    zone = secondZone,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            ZoneComparisonRiskChartCard(chart = charts.riskChart)
        }

        items(
            items = charts.metricCharts,
            key = { it.metricType.name }
        ) { chart ->
            ZoneComparisonMetricChartCard(chart = chart)
        }
    }
}

@Composable
private fun ComparisonZoneSummaryCard(
    zone: DangerZoneComparisonModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                color = Color(0xFF221814),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = zone.city,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF6E5B55),
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
                value = "${zone.priceSquareMeter} EUR/m2"
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
            color = Color(0xFF8A746D)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF221814)
        )
    }
}

@Composable
private fun ComparisonUnavailableState(
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F4F1))
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    color = Color(0xFF221814)
                )

                Text(
                    text = stringResource(R.string.comparison_unavailable_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF6E5B55)
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
