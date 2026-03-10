package alejandro.developer.data.local.entities

import androidx.room.Entity

@Entity(
    tableName = "geo_points",
    primaryKeys = ["dangerZoneId", "order"]
)
data class GeoPointEntity(
    val dangerZoneId: Int,
    val lat: Double,
    val lng: Double,
    val order: Int
)