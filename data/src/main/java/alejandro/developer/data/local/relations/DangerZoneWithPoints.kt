package alejandro.developer.data.local.relations

import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import androidx.room.Embedded
import androidx.room.Relation

data class DangerZoneWithPoints(

    @Embedded
    val zone: DangerZoneEntity,

    @Relation(
        parentColumn = "localId",
        entityColumn = "dangerZoneLocalId"
    )
    val points: List<GeoPointEntity>
)
