package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class EconomyStatsDto(

    @Json(name = "rentaBarrio")
    val rentaBarrio: Int,

    @Json(name = "rentaCiudad")
    val rentaCiudad: Int,

    @Json(name = "precioBarrio")
    val precioBarrio: Int,

    @Json(name = "precioCiudad")
    val precioCiudad: Int
)
