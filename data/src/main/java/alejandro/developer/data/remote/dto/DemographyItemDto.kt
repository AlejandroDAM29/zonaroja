package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DemographyItemDto(

    @param:Json(name = "name")
    val name: String,

    @param:Json(name = "percentage")
    val percentage: Float
)
