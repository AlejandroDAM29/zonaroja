package alejandro.developer.data.di

import alejandro.developer.data.remote.apis.GetCiudadesApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object DataApiHiltModule {
    @Provides
    fun provideGetCiudadesApi(
        retrofit: Retrofit
    ): GetCiudadesApi =
        retrofit.create(GetCiudadesApi::class.java)
}