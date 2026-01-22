package alejandro.developer.data.di

import alejandro.developer.data.repositories.FakeTextRepositoryImpl
import alejandro.developer.domain.repositories.TextRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataRepositoryHiltModule {

    @Binds
    abstract fun bindTextRepository(
        impl: FakeTextRepositoryImpl
    ): TextRepository
}