package alejandro.developer.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.Index

@Entity(
    tableName = "geo_points",
    primaryKeys = ["dangerZoneLocalId", "order"],
    indices = [Index("dangerZoneLocalId")],
    foreignKeys = [
        ForeignKey(
            entity = DangerZoneEntity::class,
            parentColumns = ["localId"],
            childColumns = ["dangerZoneLocalId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GeoPointEntity(
    val dangerZoneLocalId: Long,
    val lat: Double,
    val lng: Double,
    val order: Int
)
