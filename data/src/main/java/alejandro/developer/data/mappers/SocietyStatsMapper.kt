package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.SocietyStatsDto
import alejandro.developer.domain.models.SocietyStatsModel

fun SocietyStatsDto.toDomain() =
    SocietyStatsModel(
        paroBarrio = paroBarrio,
        paroCiudad = paroCiudad,
        pobrezaBarrio = pobrezaBarrio,
        pobrezaCiudad = pobrezaCiudad
    )