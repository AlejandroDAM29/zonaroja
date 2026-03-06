package alejandro.developer.data.remote.dto

data class StatsGraphicsDto(
    val economy: EconomyStatsDto,
    val demography: List<DemographyItemDto>,
    val society: SocietyStatsDto
)
