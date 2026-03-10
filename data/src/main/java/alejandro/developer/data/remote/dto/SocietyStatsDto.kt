package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class SocietyStatsDto(

    @Json(name = "paroBarrio")
    val hoodUnemployment: Float,

    @Json(name = "paroCiudad")
    val cityUnemployment: Float,

    @Json(name = "pobrezaBarrio")
    val hoodPoberty: Float,

    @Json(name = "pobrezaCiudad")
    val cityPoberty: Float
)
