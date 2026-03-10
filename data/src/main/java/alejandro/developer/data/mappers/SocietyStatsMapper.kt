package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.SocietyStatsDto
import alejandro.developer.domain.models.SocietyStatsModel

fun SocietyStatsDto.toDomain() =
    SocietyStatsModel(
        hoodUnemployment = hoodUnemployment,
        cityUnemployment = cityUnemployment,
        hoodPoberty = hoodPoberty,
        cityPoberty = cityPoberty
    )