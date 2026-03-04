package alejandro.developer.domain.models

data class EconomyStatsModel(
    val rentaBarrio: Int,
    val rentaCiudad: Int,
    val pobrezaBarrio: Double,
    val pobrezaCiudad: Double,
    val precioBarrio: Int,
    val precioCiudad: Int
)