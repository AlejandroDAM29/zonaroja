package alejandro.developer.data.local.converters

import alejandro.developer.domain.models.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskLevelConverterTest {

    private val converter = RiskLevelConverter()

    @Test
    fun fromRiskLevel_returnsEnumName() {
        assertEquals("HIGH", converter.fromRiskLevel(RiskLevel.HIGH))
    }

    @Test
    fun toRiskLevel_returnsMatchingEnum() {
        assertEquals(RiskLevel.MEDIUM, converter.toRiskLevel("MEDIUM"))
    }
}
