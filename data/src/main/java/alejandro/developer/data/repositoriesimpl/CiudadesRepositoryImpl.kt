package alejandro.developer.data.repositoriesimpl

import alejandro.developer.data.remote.datasources.CiudadesRemoteDataSource
import alejandro.developer.domain.repositories.CiudadesRepository
import javax.inject.Inject

class CiudadesRepositoryImpl @Inject constructor(
    private val remoteDataSource: CiudadesRemoteDataSource
) : CiudadesRepository {

    override suspend fun getCiudades(): List<String> =
        remoteDataSource.getCiudades()
}