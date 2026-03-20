package alejandro.developer.data.local

import alejandro.developer.data.local.converters.RiskLevelConverter
import alejandro.developer.data.local.daos.DangerZoneDao
import alejandro.developer.data.local.daos.NotificationDao
import alejandro.developer.data.local.entities.DangerZoneEntity
import alejandro.developer.data.local.entities.GeoPointEntity
import alejandro.developer.data.local.entities.NotificationEntity
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        DangerZoneEntity::class,
        GeoPointEntity::class,
        NotificationEntity::class
    ],
    version = 2
)
@TypeConverters(RiskLevelConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dangerZoneDao(): DangerZoneDao

    abstract fun notificationDao(): NotificationDao
}
