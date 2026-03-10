package alejandro.developer.data.local

import alejandro.developer.data.local.converters.RiskLevelConverter
import alejandro.developer.data.local.daos.DangerZoneDao
import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        DangerZoneEntity::class,
        GeoPointEntity::class
    ],
    version = 1
)
@TypeConverters(RiskLevelConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dangerZoneDao(): DangerZoneDao
}