package alejandro.developer.data.mappers

import alejandro.developer.data.remote.dto.DemographyItemDto
import alejandro.developer.domain.models.DemographyItemModel

fun DemographyItemDto.toDomain() =
    DemographyItemModel(
        name = name,
        percentage = percentage
    )