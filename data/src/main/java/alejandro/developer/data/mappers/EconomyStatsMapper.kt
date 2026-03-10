package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.EconomyStatsDto
import alejandro.developer.domain.models.EconomyStatsModel

fun EconomyStatsDto.toDomain() =
    EconomyStatsModel(
        hoodRent = hoodRent,
        cityRent = cityRent,
        hoodPrice = hoodPrice,
        cityPrice = cityPrice
    )