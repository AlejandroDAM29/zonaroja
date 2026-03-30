package alejandro.developer.data.mappers

import alejandro.developer.data.sampleDangerZoneDto
import alejandro.developer.data.sampleDangerZoneModel
import alejandro.developer.data.sampleDangerZoneWithPoints
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DangerZoneMapperTest {

    @Test
    fun dtoToDomain_mapsAllFields() {
        val dto = sampleDangerZoneDto()

        val result = dto.toDomain()

        assertEquals(dto.id, result.id)
        assertEquals(dto.zoneName, result.zoneName)
        assertEquals(dto.city, result.city)
        assertEquals(dto.povertyRiskRate, result.povertyRiskRate, 0.0)
        assertEquals(dto.unemploymentRate, result.unemploymentRate, 0.0)
        assertEquals(dto.priceSquareMeter, result.priceSquareMeter)
        assertEquals(2, result.points.size)
    }

    @Test
    fun modelToEntity_mapsDomainFieldsAndUserScope() {
        val model = sampleDangerZoneModel()

        val result = model.toEntity("user-42")

        assertEquals(model.id, result.zoneId)
        assertEquals("user-42", result.userId)
        assertEquals(model.zoneName, result.zoneName)
        assertEquals(model.city, result.city)
        assertEquals(model.riskLevel, result.riskLevel)
    }

    @Test
    fun modelToGeoEntities_usesProvidedLocalId() {
        val model = sampleDangerZoneModel()

        val result = model.toGeoEntities(99L)

        assertEquals(2, result.size)
        assertTrue(result.all { it.dangerZoneLocalId == 99L })
        assertEquals(listOf(2, 1), result.map { it.order })
    }

    @Test
    fun relationToDomain_sortsPointsByOrder() {
        val relation = sampleDangerZoneWithPoints()

        val result = relation.toDomain()

        assertEquals(listOf(1, 2), result.points.map { it.order })
        assertEquals(relation.zone.zoneName, result.zoneName)
        assertEquals(relation.zone.city, result.city)
    }
}
