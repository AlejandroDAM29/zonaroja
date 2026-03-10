package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class EconomyStatsDto(

    @Json(name = "rentaBarrio")
    val hoodRent: Int,

    @Json(name = "rentaCiudad")
    val cityRent: Int,

    @Json(name = "precioBarrio")
    val hoodPrice: Int,

    @Json(name = "precioCiudad")
    val cityPrice: Int
)
