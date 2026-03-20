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
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
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
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notifications_receivedAt ON notifications(receivedAt)"
            )
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notifications_isRead ON notifications(isRead)"
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
            .addMigrations(MIGRATION_1_2)
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
