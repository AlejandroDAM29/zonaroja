package alejandro.developer.data.di

import alejandro.developer.data.repositories.CiudadesRepositoryImpl
import alejandro.developer.data.remote.apis.GetCiudadesApi
import alejandro.developer.data.remote.datasources.CiudadesRemoteDataSource
import alejandro.developer.data.repositories.FirebaseAuthRepositoryImpl
import alejandro.developer.domain.auth.AuthRepository
import alejandro.developer.domain.main.CiudadesRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

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


}