package alejandro.developer.data.mappers

import alejandro.developer.data.sampleDangerZoneComparisonDto
import alejandro.developer.domain.models.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class DangerZoneComparisonMapperTest {

    @Test
    fun toDomain_mapsComparisonDto() {
        val dto = sampleDangerZoneComparisonDto()

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.zoneName, result.zoneName)
        assertEquals(dto.city, result.city)
        assertEquals(RiskLevel.HIGH, result.riskLevel)
        assertEquals(dto.povertyRiskRate, result.povertyRiskRate, 0.0)
        assertEquals(dto.unemploymentRate, result.unemploymentRate, 0.0)
        assertEquals(dto.priceSquareMeter, result.priceSquareMeter)
    }
}
