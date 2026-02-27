package alejandro.developer.zonaroja.di

import alejandro.developer.domain.repositories.LocationSearchRepository
import alejandro.developer.zonaroja.geocoder.LocationSearchRepositoryImpl
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {
    @Provides
    fun provideLocationSearchRepository(
        @ApplicationContext context: Context
    ): LocationSearchRepository {
        return LocationSearchRepositoryImpl(context)
    }
}