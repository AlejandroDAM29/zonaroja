package alejandro.developer.data.local.daos

import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.local.relations.DangerZoneWithPoints
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DangerZoneDao {

    @Transaction
    @Query("SELECT * FROM danger_zones")
    suspend fun getAllDangerZones(): List<DangerZoneWithPoints>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDangerZone(zone: DangerZoneEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeoPoints(points: List<GeoPointEntity>)
}