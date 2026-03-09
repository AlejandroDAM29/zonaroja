package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class SocietyStatsDto(

    @Json(name = "paroBarrio")
    val paroBarrio: Float,

    @Json(name = "paroCiudad")
    val paroCiudad: Float,

    @Json(name = "pobrezaBarrio")
    val pobrezaBarrio: Float,

    @Json(name = "pobrezaCiudad")
    val pobrezaCiudad: Float
)
