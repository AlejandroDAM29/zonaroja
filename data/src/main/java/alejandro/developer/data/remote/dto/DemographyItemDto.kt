package alejandro.developer.data.remote.dto

import com.squareup.moshi.Json

data class DemographyItemDto(

    @Json(name = "name")
    val name: String,

    @Json(name = "percentage")
    val percentage: Float
)
