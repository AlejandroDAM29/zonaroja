package alejandro.developer.data.remote.dto

data class EconomyStatsDto(
    val rentaBarrio: Int,
    val rentaCiudad: Int,
    val pobrezaBarrio: Double,
    val pobrezaCiudad: Double,
    val precioBarrio: Int,
    val precioCiudad: Int
)
