package alejandro.developer.domain.models

data class ZoneComparisonChartsUiModel(
    val riskChart: ZoneComparisonRiskChartUiModel,
    val metricCharts: List<ZoneComparisonBarChartUiModel>
)

data class ZoneComparisonRiskChartUiModel(
    val entries: List<ZoneComparisonRiskChartEntryUiModel>,
    val maxScore: Float = 3f
)

data class ZoneComparisonRiskChartEntryUiModel(
    val label: String,
    val city: String,
    val riskLevel: RiskLevel,
    val score: Float
)

data class ZoneComparisonBarChartUiModel(
    val metricType: ZoneComparisonMetricType,
    val maxValue: Float,
    val entries: List<ZoneComparisonBarChartEntryUiModel>
)

data class ZoneComparisonBarChartEntryUiModel(
    val label: String,
    val city: String,
    val value: Float,
    val formattedValue: String
)

enum class ZoneComparisonMetricType {
    POVERTY_RISK,
    UNEMPLOYMENT,
    PRICE_SQUARE_METER
}