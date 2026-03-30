package alejandro.developer.data.di

import alejandro.developer.core.runtime.AppDataMode
import alejandro.developer.data.datasources.DangerZoneDataSource
import alejandro.developer.data.datasources.ModsDangerZoneDataSource
import alejandro.developer.data.remote.datasources.FeatureFlagsRepositoryImpl
import alejandro.developer.data.remote.datasources.DangerZoneRemoteDataSource
import alejandro.developer.data.repositoriesimpl.DangerZoneRepositoryImpl
import alejandro.developer.data.repositoriesimpl.FirebaseAuthRepositoryImpl
import alejandro.developer.data.repositoriesimpl.GraphicsRepositoryImpl
import alejandro.developer.data.repositoriesimpl.ModsAuthRepositoryImpl
import alejandro.developer.data.repositoriesimpl.NotificationRepositoryImpl
import alejandro.developer.data.repositoriesimpl.UserSettingsRepositoryImpl
import alejandro.developer.domain.repositories.AuthRepository
import alejandro.developer.domain.repositories.FeatureFlagsRepository
import alejandro.developer.domain.repositories.DangerZoneRepository
import alejandro.developer.domain.repositories.GraphicsRepository
import alejandro.developer.domain.repositories.NotificationRepository
import alejandro.developer.domain.repositories.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataRepositoryHiltModule {

    @Binds
    abstract fun bindFeatureFlagsRepository(
        impl: FeatureFlagsRepositoryImpl
    ): FeatureFlagsRepository

    @Binds
    @Singleton
    abstract fun bindDangerZoneRepository(
        impl: DangerZoneRepositoryImpl
    ): DangerZoneRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    abstract fun bindGraphicsRepository(
        impl: GraphicsRepositoryImpl
    ): GraphicsRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        impl: UserSettingsRepositoryImpl
    ): UserSettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideAuthRepository(
            appDataMode: AppDataMode,
            firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl,
            modsAuthRepositoryImpl: ModsAuthRepositoryImpl
        ): AuthRepository {
            return if (appDataMode.usesModsData) {
                modsAuthRepositoryImpl
            } else {
                firebaseAuthRepositoryImpl
            }
        }

        @Provides
        @Singleton
        fun provideDangerZoneDataSource(
            appDataMode: AppDataMode,
            remoteDataSource: DangerZoneRemoteDataSource,
            modsDataSource: ModsDangerZoneDataSource
        ): DangerZoneDataSource {
            return if (appDataMode.usesModsData) {
                modsDataSource
            } else {
                remoteDataSource
            }
        }
    }
}
