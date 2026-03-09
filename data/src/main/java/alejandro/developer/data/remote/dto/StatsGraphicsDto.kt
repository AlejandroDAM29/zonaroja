package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class StatsGraphicsDto(
    @Json(name = "economy")
    val economy: EconomyStatsDto,

    @Json(name = "society")
    val society: SocietyStatsDto,

    @Json(name = "demography")
    val demography: List<DemographyItemDto>
)
