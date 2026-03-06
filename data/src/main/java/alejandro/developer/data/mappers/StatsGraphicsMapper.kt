package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.StatsGraphicsDto
import alejandro.developer.domain.models.StatsGraphicsModel

fun StatsGraphicsDto.toDomain() =
    StatsGraphicsModel(
        economy = economy.toDomain(),
        demography = demography.map { it.toDomain() },
        society = society.toDomain()
    )