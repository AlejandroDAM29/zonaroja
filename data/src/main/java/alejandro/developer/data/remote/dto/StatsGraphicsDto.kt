package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatsGraphicsDto(
    @param:Json(name = "economy")
    val economy: EconomyStatsDto,

    @param:Json(name = "society")
    val society: SocietyStatsDto,

    @param:Json(name = "demography")
    val demography: List<DemographyItemDto>
)
