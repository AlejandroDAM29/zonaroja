package alejandro.developer.data.di

import alejandro.developer.data.remote.AuthInterceptor
import alejandro.developer.data.remote.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataNetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://alejandroexpdeveloper.com/zona_roja_app_api/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    @Provides
    fun provideOkHttp(
        tokenStore: TokenStore
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { tokenStore.token })
            .build()
}