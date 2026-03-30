package alejandro.developer.zonaroja.di

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.zonaroja.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDataModeModule {

    @Provides
    @Singleton
    fun provideAppDataMode(): AppDataMode {
        return if (BuildConfig.USE_MODS) AppDataMode.MODS else AppDataMode.BACKEND
    }
}
