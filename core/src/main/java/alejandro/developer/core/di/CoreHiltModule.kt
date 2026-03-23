package alejandro.developer.core.di

import alejandro.developer.core.network.AndroidNetworkMonitor
import alejandro.developer.core.network.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreHiltModule {

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: AndroidNetworkMonitor
    ): NetworkMonitor
}
