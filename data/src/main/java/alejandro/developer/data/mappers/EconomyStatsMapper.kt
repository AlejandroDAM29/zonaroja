package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.EconomyStatsDto
import alejandro.developer.domain.models.EconomyStatsModel

fun EconomyStatsDto.toDomain() =
    EconomyStatsModel(
        rentaBarrio = rentaBarrio,
        rentaCiudad = rentaCiudad,
        pobrezaBarrio = pobrezaBarrio,
        pobrezaCiudad = pobrezaCiudad,
        precioBarrio = precioBarrio,
        precioCiudad = precioCiudad
    )