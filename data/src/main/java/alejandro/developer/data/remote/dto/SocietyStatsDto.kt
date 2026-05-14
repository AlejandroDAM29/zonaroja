package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocietyStatsDto(

    @param:Json(name = "paroBarrio")
    val hoodUnemployment: Float,

    @param:Json(name = "paroCiudad")
    val cityUnemployment: Float,

    @param:Json(name = "pobrezaBarrio")
    val hoodPoberty: Float,

    @param:Json(name = "pobrezaCiudad")
    val cityPoberty: Float
)
