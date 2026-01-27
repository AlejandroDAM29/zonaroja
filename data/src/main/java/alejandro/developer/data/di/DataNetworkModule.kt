package alejandro.developer.data.di

import alejandro.developer.core.auth.AuthInterceptor
import alejandro.developer.core.auth.AuthRetryInterceptor
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
    fun provideOkHttp(
        authInterceptor: AuthInterceptor,
        authRetryInterceptor: AuthRetryInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(authRetryInterceptor)
            .build()

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
}