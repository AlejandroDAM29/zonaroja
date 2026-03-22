package alejandro.developer.data.di

import alejandro.developer.data.local.AppDatabase
import alejandro.developer.data.local.daos.DangerZoneDao
import alejandro.developer.data.local.daos.NotificationDao
import android.content.Context
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    remoteMessageId TEXT,
                    title TEXT NOT NULL,
                    body TEXT NOT NULL,
                    imageUrl TEXT,
                    receivedAt INTEGER NOT NULL,
                    isRead INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notifications_receivedAt ON notifications(receivedAt)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notifications_isRead ON notifications(isRead)"
            )
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS danger_zones_new (
                    localId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    zoneId INTEGER NOT NULL,
                    userId TEXT NOT NULL,
                    zoneName TEXT NOT NULL,
                    city TEXT NOT NULL,
                    riskLevel TEXT NOT NULL,
                    povertyRiskRate REAL NOT NULL,
                    unemploymentRate REAL NOT NULL,
                    priceSquareMeter INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO danger_zones_new (
                    zoneId,
                    userId,
                    zoneName,
                    city,
                    riskLevel,
                    povertyRiskRate,
                    unemploymentRate,
                    priceSquareMeter
                )
                SELECT
                    id,
                    '__guest__',
                    zoneName,
                    city,
                    riskLevel,
                    povertyRiskRate,
                    unemploymentRate,
                    priceSquareMeter
                FROM danger_zones
                """.trimIndent()
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS geo_points_new (
                    dangerZoneLocalId INTEGER NOT NULL,
                    lat REAL NOT NULL,
                    lng REAL NOT NULL,
                    `order` INTEGER NOT NULL,
                    PRIMARY KEY(dangerZoneLocalId, `order`),
                    FOREIGN KEY(dangerZoneLocalId) REFERENCES danger_zones_new(localId) ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO geo_points_new (dangerZoneLocalId, lat, lng, `order`)
                SELECT
                    dz_new.localId,
                    gp.lat,
                    gp.lng,
                    gp.`order`
                FROM geo_points gp
                INNER JOIN danger_zones dz_old ON dz_old.id = gp.dangerZoneId
                INNER JOIN danger_zones_new dz_new ON dz_new.zoneId = dz_old.id
                    AND dz_new.userId = '__guest__'
                """.trimIndent()
            )
            db.execSQL("DROP TABLE geo_points")
            db.execSQL("DROP TABLE danger_zones")
            db.execSQL("ALTER TABLE danger_zones_new RENAME TO danger_zones")
            db.execSQL("ALTER TABLE geo_points_new RENAME TO geo_points")
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_danger_zones_zoneId_userId ON danger_zones(zoneId, userId)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_danger_zones_userId ON danger_zones(userId)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_geo_points_dangerZoneLocalId ON geo_points(dangerZoneLocalId)"
            )
            db.execSQL(
                "ALTER TABLE notifications ADD COLUMN userId TEXT NOT NULL DEFAULT '__guest__'"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notifications_userId ON notifications(userId)"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "danger_zone_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    fun provideDangerZoneDao(
        database: AppDatabase
    ): DangerZoneDao {
        return database.dangerZoneDao()
    }

    @Provides
    fun provideNotificationDao(
        database: AppDatabase
    ): NotificationDao {
        return database.notificationDao()
    }
}
