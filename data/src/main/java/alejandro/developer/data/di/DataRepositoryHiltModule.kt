package alejandro.developer.data.di

import alejandro.developer.data.remote.datasources.FeatureFlagsRepositoryImpl
import alejandro.developer.data.repositoriesimpl.CiudadesRepositoryImpl
import alejandro.developer.data.repositoriesimpl.DangerZoneRepositoryImpl
import alejandro.developer.data.repositoriesimpl.FirebaseAuthRepositoryImpl
import alejandro.developer.domain.repositories.AuthRepository
import alejandro.developer.domain.repositories.FeatureFlagsRepository
import alejandro.developer.domain.repositories.CiudadesRepository
import alejandro.developer.domain.repositories.DangerZoneRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataRepositoryHiltModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindCiudadesRepository(
        impl: CiudadesRepositoryImpl
    ): CiudadesRepository

    @Binds
    abstract fun bindFeatureFlagsRepository(
        impl: FeatureFlagsRepositoryImpl
    ): FeatureFlagsRepository

    @Binds
    @Singleton
    abstract fun bindDangerZoneRepository(
        impl: DangerZoneRepositoryImpl
    ): DangerZoneRepository
}