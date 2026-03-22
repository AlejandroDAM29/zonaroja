package alejandro.developer.data.local.daos

import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.local.relations.DangerZoneWithPoints
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DangerZoneDao {

    @Transaction
    @Query("SELECT * FROM danger_zones WHERE userId = :userId ORDER BY localId DESC")
    suspend fun getAllDangerZones(userId: String): List<DangerZoneWithPoints>

    @Transaction
    @Query("SELECT * FROM danger_zones WHERE userId = :userId ORDER BY localId DESC")
    fun observeAllDangerZones(userId: String): Flow<List<DangerZoneWithPoints>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDangerZone(zone: DangerZoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeoPoints(points: List<GeoPointEntity>)

    @Transaction
    @Query("SELECT zoneId FROM danger_zones WHERE userId = :userId")
    fun getSavedZoneIds(userId: String): Flow<List<Int>>

    @Query("DELETE FROM danger_zones WHERE zoneId = :zoneId AND userId = :userId")
    suspend fun deleteDangerZone(zoneId: Int, userId: String)
}
