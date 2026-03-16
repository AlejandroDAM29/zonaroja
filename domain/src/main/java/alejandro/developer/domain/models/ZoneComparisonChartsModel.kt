package alejandro.developer.domain.models

data class ZoneComparisonChartsUiModel(
    val riskChart: ZoneComparisonRiskChartUiModel,
    val metricCharts: List<ZoneComparisonBarChartUiModel>
)

interface ZoneComparisonChartEntryUiModel {
    val label: String
    val city: String
    val value: Float
    val formattedValue: String
}

data class ZoneComparisonRiskChartUiModel(
    val entries: List<ZoneComparisonRiskChartEntryUiModel>,
    val maxScore: Float = 3f
)

data class ZoneComparisonRiskChartEntryUiModel(
    override val label: String,
    override val city: String,
    val riskLevel: RiskLevel,
    val score: Float,
    val formattedScore: String
) : ZoneComparisonChartEntryUiModel {
    override val value: Float = score
    override val formattedValue: String = formattedScore
}

data class ZoneComparisonBarChartUiModel(
    val metricType: ZoneComparisonMetricType,
    val maxValue: Float,
    val entries: List<ZoneComparisonBarChartEntryUiModel>
)

data class ZoneComparisonBarChartEntryUiModel(
    override val label: String,
    override val city: String,
    override val value: Float,
    override val formattedValue: String
) : ZoneComparisonChartEntryUiModel

enum class ZoneComparisonMetricType {
    POVERTY_RISK,
    UNEMPLOYMENT,
    PRICE_SQUARE_METER
}
