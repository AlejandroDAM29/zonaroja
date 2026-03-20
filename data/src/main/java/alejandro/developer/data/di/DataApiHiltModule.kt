package alejandro.developer.data.di

import alejandro.developer.data.remote.apis.DangerZoneApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object DataApiHiltModule {
    @Provides
    fun provideGetDangerZoneApi(
        retrofit: Retrofit
    ): DangerZoneApi =
        retrofit.create(DangerZoneApi::class.java)
}
