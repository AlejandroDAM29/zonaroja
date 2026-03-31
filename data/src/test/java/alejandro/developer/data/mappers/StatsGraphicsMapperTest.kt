package alejandro.developer.data.mappers

import alejandro.developer.data.sampleDemographyItemDtos
import alejandro.developer.data.sampleEconomyStatsDto
import alejandro.developer.data.sampleSocietyStatsDto
import alejandro.developer.data.sampleStatsGraphicsDto
import org.junit.Assert.assertEquals
import org.junit.Test

class StatsGraphicsMapperTest {

    @Test
    fun economyToDomain_mapsAllFields() {
        val dto = sampleEconomyStatsDto()

        val result = dto.toDomain()

        assertEquals(dto.hoodRent, result.hoodRent)
        assertEquals(dto.cityRent, result.cityRent)
        assertEquals(dto.hoodPrice, result.hoodPrice)
        assertEquals(dto.cityPrice, result.cityPrice)
    }

    @Test
    fun societyToDomain_mapsAllFields() {
        val dto = sampleSocietyStatsDto()

        val result = dto.toDomain()

        assertEquals(dto.hoodUnemployment, result.hoodUnemployment)
        assertEquals(dto.cityUnemployment, result.cityUnemployment)
        assertEquals(dto.hoodPoberty, result.hoodPoberty)
        assertEquals(dto.cityPoberty, result.cityPoberty)
    }

    @Test
    fun demographyItemToDomain_mapsAllFields() {
        val dto = sampleDemographyItemDtos().first()

        val result = dto.toDomain()

        assertEquals(dto.name, result.name)
        assertEquals(dto.percentage, result.percentage)
    }

    @Test
    fun statsGraphicsToDomain_mapsNestedModels() {
        val dto = sampleStatsGraphicsDto()

        val result = dto.toDomain()

        assertEquals(dto.economy.hoodRent, result.economy.hoodRent)
        assertEquals(dto.society.cityPoberty, result.society.cityPoberty)
        assertEquals(dto.demography.map { it.name }, result.demography.map { it.name })
    }
}
