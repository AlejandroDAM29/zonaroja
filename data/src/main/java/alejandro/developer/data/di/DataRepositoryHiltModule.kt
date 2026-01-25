package alejandro.developer.data.di

import alejandro.developer.data.CiudadesRepositoryImpl
import alejandro.developer.data.remote.apis.GetCiudadesApi
import alejandro.developer.data.remote.datasources.CiudadesRemoteDataSource
import alejandro.developer.domain.repositories.CiudadesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object DataRepositoryHiltModule {

    @Provides
    fun provideGetCiudadesApi(
        retrofit: Retrofit
    ): GetCiudadesApi =
        retrofit.create(GetCiudadesApi::class.java)

    @Provides
    fun provideCiudadesRepository(
        remote: CiudadesRemoteDataSource
    ): CiudadesRepository =
        CiudadesRepositoryImpl(remote)


}