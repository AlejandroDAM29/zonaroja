package alejandro.developer.data.mappers

import alejandro.developer.domain.models.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskLevelMapperTest {

    @Test
    fun toRiskLevel_handlesKnownValuesIgnoringCase() {
        assertEquals(RiskLevel.LOW, "low".toRiskLevel())
        assertEquals(RiskLevel.MEDIUM, "MEDIUM".toRiskLevel())
        assertEquals(RiskLevel.HIGH, "High".toRiskLevel())
    }

    @Test
    fun toRiskLevel_defaultsToLow_forUnknownValues() {
        assertEquals(RiskLevel.LOW, "unexpected".toRiskLevel())
    }
}
