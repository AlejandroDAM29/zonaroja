package alejandro.developer.zonaroja.ui.mappers

import alejandro.developer.domain.models.RiskLevel
import alejandro.developer.domain.models.ZoneComparisonMetricType
import alejandro.developer.zonaroja.sampleComparisonModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ZoneComparisonChartMapperTest {

    @Test
    fun buildZoneComparisonCharts_mapsRiskAndMetricCharts() {
        val first = sampleComparisonModel(
            id = 1,
            zoneName = "Centro",
            city = "Madrid",
            riskLevel = RiskLevel.LOW,
            povertyRiskRate = 12.4,
            unemploymentRate = 7.1,
            priceSquareMeter = 4100
        )
        val second = sampleComparisonModel(
            id = 2,
            zoneName = "Vallecas",
            city = "Madrid",
            riskLevel = RiskLevel.HIGH,
            povertyRiskRate = 21.5,
            unemploymentRate = 11.2,
            priceSquareMeter = 2800
        )

        val result = buildZoneComparisonCharts(first, second)

        assertEquals(listOf("Bajo", "Alto"), result.riskChart.entries.map { it.formattedScore })
        assertEquals(listOf(1f, 3f), result.riskChart.entries.map { it.score })
        assertEquals(3, result.metricCharts.size)
        assertEquals(ZoneComparisonMetricType.POVERTY_RISK, result.metricCharts[0].metricType)
        assertEquals(21.5f, result.metricCharts[0].maxValue)
        assertEquals("12.4%", result.metricCharts[0].entries[0].formattedValue)
        assertEquals("2800", result.metricCharts[2].entries[1].formattedValue)
    }

    @Test
    fun buildZoneComparisonCharts_ensuresMetricMaxValueIsAtLeastOne() {
        val first = sampleComparisonModel(
            id = 1,
            zoneName = "A",
            city = "Madrid",
            riskLevel = RiskLevel.MEDIUM,
            povertyRiskRate = 0.0,
            unemploymentRate = 0.0,
            priceSquareMeter = 0
        )
        val second = sampleComparisonModel(
            id = 2,
            zoneName = "B",
            city = "Madrid",
            riskLevel = RiskLevel.MEDIUM,
            povertyRiskRate = 0.0,
            unemploymentRate = 0.0,
            priceSquareMeter = 0
        )

        val result = buildZoneComparisonCharts(first, second)

        assertTrue(result.metricCharts.all { it.maxValue == 1f })
    }
}
