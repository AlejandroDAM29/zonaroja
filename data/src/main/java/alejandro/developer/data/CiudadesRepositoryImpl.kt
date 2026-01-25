package alejandro.developer.data

import alejandro.developer.data.remote.datasources.CiudadesRemoteDataSource
import alejandro.developer.domain.repositories.CiudadesRepository

class CiudadesRepositoryImpl(
    private val remoteDataSource: CiudadesRemoteDataSource
) : CiudadesRepository {

    override suspend fun getCiudades(): List<String> =
        remoteDataSource.getCiudades()
}
