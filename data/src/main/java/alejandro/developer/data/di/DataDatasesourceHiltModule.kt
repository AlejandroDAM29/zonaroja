package alejandro.developer.data.di

import alejandro.developer.data.remote.apis.GetCiudadesApi
import alejandro.developer.data.remote.datasources.CiudadesRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataDatasesourceHiltModule {

    @Provides
    fun provideCiudadesRemoteDataSource(
        api: GetCiudadesApi
    ): CiudadesRemoteDataSource =
        CiudadesRemoteDataSource(api)
}