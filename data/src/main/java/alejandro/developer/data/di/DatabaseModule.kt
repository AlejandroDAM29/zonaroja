package alejandro.developer.data.di

import alejandro.developer.data.local.AppDatabase
import alejandro.developer.data.local.daos.DangerZoneDao
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "danger_zone_db"
        ).build()
    }

    @Provides
    fun provideDangerZoneDao(
        database: AppDatabase
    ): DangerZoneDao {
        return database.dangerZoneDao()
    }
}