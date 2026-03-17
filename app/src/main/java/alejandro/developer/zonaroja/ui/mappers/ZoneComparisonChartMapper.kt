package alejandro.developer.zonaroja.ui.mappers

import alejandro.developer.domain.models.DangerZoneComparisonModel
import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.ZoneComparisonBarChartEntryUiModel
import alejandro.developer.domain.models.ZoneComparisonBarChartUiModel
import alejandro.developer.domain.models.ZoneComparisonChartsUiModel
import alejandro.developer.domain.models.ZoneComparisonMetricType
import alejandro.developer.domain.models.ZoneComparisonRiskChartEntryUiModel
import alejandro.developer.domain.models.ZoneComparisonRiskChartUiModel
import java.util.Locale
import kotlin.math.max

fun buildZoneComparisonCharts(
    firstZone: DangerZoneComparisonModel,
    secondZone: DangerZoneComparisonModel
): ZoneComparisonChartsUiModel {
    return ZoneComparisonChartsUiModel(
        riskChart = ZoneComparisonRiskChartUiModel(
            entries = listOf(
                ZoneComparisonRiskChartEntryUiModel(
                    label = firstZone.zoneName,
                    city = firstZone.city,
                    riskLevel = firstZone.riskLevel,
                    score = firstZone.riskLevel.toChartScore(),
                    formattedScore = firstZone.riskLevel.toChartLabel()
                ),
                ZoneComparisonRiskChartEntryUiModel(
                    label = secondZone.zoneName,
                    city = secondZone.city,
                    riskLevel = secondZone.riskLevel,
                    score = secondZone.riskLevel.toChartScore(),
                    formattedScore = secondZone.riskLevel.toChartLabel()
                )
            )
        ),
        metricCharts = listOf(
            ZoneComparisonBarChartUiModel(
                metricType = ZoneComparisonMetricType.POVERTY_RISK,
                maxValue = max(
                    firstZone.povertyRiskRate.toFloat(),
                    secondZone.povertyRiskRate.toFloat()
                ).coerceAtLeast(1f),
                entries = listOf(
                    firstZone.toBarEntry(
                        value = firstZone.povertyRiskRate,
                        formattedValue = firstZone.povertyRiskRate.toPercentLabel()
                    ),
                    secondZone.toBarEntry(
                        value = secondZone.povertyRiskRate,
                        formattedValue = secondZone.povertyRiskRate.toPercentLabel()
                    )
                )
            ),
            ZoneComparisonBarChartUiModel(
                metricType = ZoneComparisonMetricType.UNEMPLOYMENT,
                maxValue = max(
                    firstZone.unemploymentRate.toFloat(),
                    secondZone.unemploymentRate.toFloat()
                ).coerceAtLeast(1f),
                entries = listOf(
                    firstZone.toBarEntry(
                        value = firstZone.unemploymentRate,
                        formattedValue = firstZone.unemploymentRate.toPercentLabel()
                    ),
                    secondZone.toBarEntry(
                        value = secondZone.unemploymentRate,
                        formattedValue = secondZone.unemploymentRate.toPercentLabel()
                    )
                )
            ),
            ZoneComparisonBarChartUiModel(
                metricType = ZoneComparisonMetricType.PRICE_SQUARE_METER,
                maxValue = max(
                    firstZone.priceSquareMeter.toFloat(),
                    secondZone.priceSquareMeter.toFloat()
                ).coerceAtLeast(1f),
                entries = listOf(
                    firstZone.toBarEntry(
                        value = firstZone.priceSquareMeter.toDouble(),
                        formattedValue = firstZone.priceSquareMeter.toString()
                    ),
                    secondZone.toBarEntry(
                        value = secondZone.priceSquareMeter.toDouble(),
                        formattedValue = secondZone.priceSquareMeter.toString()
                    )
                )
            )
        )
    )
}

private fun DangerZoneComparisonModel.toBarEntry(
    value: Double,
    formattedValue: String
): ZoneComparisonBarChartEntryUiModel {
    return ZoneComparisonBarChartEntryUiModel(
        label = zoneName,
        city = city,
        value = value.toFloat(),
        formattedValue = formattedValue
    )
}

private fun Double.toPercentLabel(): String {
    return String.format(Locale.US, "%.1f%%", this)
}

private fun RiskLevel.toChartScore(): Float {
    return when (this) {
        RiskLevel.LOW -> 1f
        RiskLevel.MEDIUM -> 2f
        RiskLevel.HIGH -> 3f
    }
}

private fun RiskLevel.toChartLabel(): String {
    return when (this) {
        RiskLevel.LOW -> "Bajo"
        RiskLevel.MEDIUM -> "Medio"
        RiskLevel.HIGH -> "Alto"
    }
}
