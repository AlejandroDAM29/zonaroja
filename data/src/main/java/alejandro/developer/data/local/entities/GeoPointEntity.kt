package alejandro.developer.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

import androidx.room.Index

@Entity(
    tableName = "geo_points",
    primaryKeys = ["dangerZoneId", "order"],
    indices = [Index("dangerZoneId")],
    foreignKeys = [
        ForeignKey(
            entity = DangerZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["dangerZoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GeoPointEntity(
    val dangerZoneId: Int,
    val lat: Double,
    val lng: Double,
    val order: Int
)