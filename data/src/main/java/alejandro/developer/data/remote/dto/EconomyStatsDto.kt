package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EconomyStatsDto(

    @param:Json(name = "rentaBarrio")
    val hoodRent: Int,

    @param:Json(name = "rentaCiudad")
    val cityRent: Int,

    @param:Json(name = "precioBarrio")
    val hoodPrice: Int,

    @param:Json(name = "precioCiudad")
    val cityPrice: Int
)
